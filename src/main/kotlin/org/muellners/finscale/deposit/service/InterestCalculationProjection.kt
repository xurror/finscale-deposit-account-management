package org.muellners.finscale.deposit.service

import com.google.common.collect.Sets
import java.math.BigDecimal
import java.math.MathContext
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import java.util.stream.IntStream
import org.apache.commons.lang3.RandomStringUtils
import org.axonframework.eventhandling.EventHandler
import org.muellners.finscale.deposit.domain.enumeration.InterestPayable
import org.muellners.finscale.deposit.domain.enumeration.Type
import org.muellners.finscale.deposit.domain.productInstance.commands.ProductDefinition
import org.muellners.finscale.deposit.event.AccruedEvent
import org.muellners.finscale.deposit.event.DividendDistributedEvent
import org.muellners.finscale.deposit.event.PaidInterestEvent
import org.muellners.finscale.deposit.repository.*
import org.muellners.finscale.deposit.view.AccruedInterestView
import org.muellners.finscale.deposit.view.DividendDistributionView
import org.muellners.finscale.deposit.view.ProductDefinitionView
import org.muellners.finscale.deposit.view.ProductInstanceView
import org.muellners.finscale.deposit.view.TermView
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.threeten.extra.YearQuarter

@Component
class InterestCalculationProjector(
    private val productDefinitionViewRepository: ProductDefinitionViewRepository,
    private val productInstanceViewRepository: ProductInstanceViewRepository,
    private val termViewRepository: TermViewRepository,
    private val accruedInterestViewRepository: AccruedInterestViewRepository,
    private val dividendDistributionViewRepository: DividendDistributionViewRepository
) {
    @EventHandler
    fun on(event: AccruedEvent): String {
        val accrualDate = event.dueDate
        val productDefinitions: List<ProductDefinitionView> = productDefinitionViewRepository.findAll()
        productDefinitions.forEach(Consumer<ProductDefinitionView> { productDefinitionView: ProductDefinitionView ->
            if (accruableProduct(productDefinitionView)) {
                val accruedValues = ArrayList<Double>()
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                val productInstances: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                productInstances.forEach(Consumer<ProductInstanceView> { productInstanceView: ProductInstanceView ->
                    if (productInstanceView.state!!.equals(ACTIVE)) {
                        val account: Account = accountingService.findAccount(productInstanceView.accountIdentifier)
                        if (account.getBalance() > 0.00) {
                            val balance: BigDecimal = BigDecimal.valueOf(account.getBalance())
                            val rate: BigDecimal = BigDecimal.valueOf(productDefinitionView.interest!!)
                                .divide(BigDecimal.valueOf(100), INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN)
                            val accruedInterest = accruedInterest(balance, rate,
                                periodOfInterestPayable(termView.interestPayable.toString()), accrualDate!!.lengthOfYear())
                            if (accruedInterest.compareTo(BigDecimal.ZERO) > 0) {
                                val doubleValue = accruedInterest.setScale(5, BigDecimal.ROUND_HALF_EVEN).toDouble()
                                accruedValues.add(doubleValue)
                                val optionalAccruedInterest: Optional<AccruedInterestView> = accruedInterestViewRepository.findByCustomerAccountIdentifier(account.getIdentifier())
                                if (optionalAccruedInterest.isPresent()) {
                                    val accruedInterestView: AccruedInterestView = optionalAccruedInterest.get()
                                    accruedInterestView.amount = accruedInterestView.amount?.plus(doubleValue)
                                    accruedInterestViewRepository.save(accruedInterestView)
                                } else {
                                    val accruedInterestView = AccruedInterestView()
                                    accruedInterestView.accrueAccountIdentifier = productDefinitionView.accrueAccountIdentifier
                                    accruedInterestView.customerAccountIdentifier = account.getIdentifier()
                                    accruedInterestView.amount = doubleValue
                                    accruedInterestViewRepository.save(accruedInterestView)
                                }
                            }
                        }
                    }
                })
                val roundedAmount = BigDecimal.valueOf(accruedValues.parallelStream().reduce(0.00) { a: Double, b: Double -> java.lang.Double.sum(a, b) })
                    .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
                val cashToAccrueJournalEntry = JournalEntry()
                cashToAccrueJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32))
                cashToAccrueJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())))
                cashToAccrueJournalEntry.setTransactionType("INTR")
                cashToAccrueJournalEntry.setClerk(UserContextHolder.checkedGetUser())
                cashToAccrueJournalEntry.setNote("Daily accrual for product " + productDefinitionView.identifier.toString() + ".")
                val cashDebtor = Debtor()
                cashDebtor.setAccountNumber(productDefinitionView.cashAccountIdentifier)
                cashDebtor.setAmount(roundedAmount)
                cashToAccrueJournalEntry.setDebtors(Sets.newHashSet(cashDebtor))
                val accrueCreditor = Creditor()
                accrueCreditor.setAccountNumber(productDefinitionView.accrueAccountIdentifier)
                accrueCreditor.setAmount(roundedAmount)
                cashToAccrueJournalEntry.setCreditors(Sets.newHashSet(accrueCreditor))
                accountingService.post(cashToAccrueJournalEntry)
            }
        })
        return DateConverter.toIsoString(accrualDate)
    }

    @EventHandler
    fun on(event: PaidInterestEvent): String {
        val productDefinitionEntities: List<ProductDefinitionView> = productDefinitionViewRepository.findAll()
        productDefinitionEntities.forEach(Consumer<ProductDefinitionView> { productDefinitionView: ProductDefinitionView ->
            if (productDefinitionView.active!! &&
                productDefinitionView.type!! != Type.SHARE) {
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                if (shouldPayInterest(termView.interestPayable.toString(), event.date!!)) {
                    val productInstances: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                    productInstances.forEach(Consumer<ProductInstanceView> { productInstanceView: ProductInstanceView ->
                        val optionalAccruedInterestView: Optional<AccruedInterestView> = accruedInterestViewRepository.findByCustomerAccountIdentifier(productInstanceView.accountIdentifier!!)
                        if (optionalAccruedInterestView.isPresent) {
                            val accruedInterestView: AccruedInterestView = optionalAccruedInterestView.get()
                            val roundedAmount: String = BigDecimal.valueOf(accruedInterestView.amount!!)
                                .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
                            val accrueToExpenseJournalEntry = JournalEntry()
                            accrueToExpenseJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32))
                            accrueToExpenseJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())))
                            accrueToExpenseJournalEntry.setTransactionType("INTR")
                            accrueToExpenseJournalEntry.setClerk(UserContextHolder.checkedGetUser())
                            accrueToExpenseJournalEntry.setNote("Interest paid.")
                            val accrueDebtor = Debtor()
                            accrueDebtor.setAccountNumber(accruedInterestView.accrueAccountIdentifier)
                            accrueDebtor.setAmount(roundedAmount)
                            accrueToExpenseJournalEntry.setDebtors(Sets.newHashSet(accrueDebtor))
                            val expenseCreditor = Creditor()
                            expenseCreditor.setAccountNumber(productDefinitionView.expenseAccountIdentifier)
                            expenseCreditor.setAmount(roundedAmount)
                            accrueToExpenseJournalEntry.setCreditors(Sets.newHashSet(expenseCreditor))
                            accruedInterestViewRepository.delete(accruedInterestView)
                            accountingService.post(accrueToExpenseJournalEntry)
                            payoutInterest(
                                productDefinitionView.expenseAccountIdentifier!!,
                                accruedInterestView.customerAccountIdentifier!!,
                                roundedAmount
                            )
                        }
                    })
                }
            }
        })
        return EventConstants.INTEREST_PAYED
    }

    @EventHandler
    fun on(event: DividendDistributedEvent): ProductDefinition? {
        val optionalProductDefinition: Optional<ProductDefinitionView> = productDefinitionViewRepository.findByIdentifier(event.productDefinition!!)
        if (optionalProductDefinition.isPresent()) {
            val productDefinitionView: ProductDefinitionView = optionalProductDefinition.get()
            if (productDefinitionView.active!!) {
                val rate: BigDecimal = BigDecimal.valueOf(event.rate!!)
                val termView: TermView = termViewRepository.findByProductDefinitionView(productDefinitionView)
                val dateRanges = dateRanges(event.dueDate!!, termView.interestPayable!!.toString())
                val productInstanceEntities: List<ProductInstanceView> = productInstanceViewRepository.findByProductDefinitionView(productDefinitionView)
                productInstanceEntities.forEach { productInstanceView: ProductInstanceView ->
                    if (productInstanceView.state.equals(ACTIVE)) {
                        val account: Account = accountingService.findAccount(productInstanceView.accountIdentifier)
                        val startDate: LocalDate = event.dueDate!!.plusDays(1)
                        val now: LocalDate = LocalDate.now(Clock.systemUTC())
                        val findCurrentEntries: String = DateConverter.toIsoString(startDate).toString() + ".." + DateConverter.toIsoString(now)
                        val currentAccountEntries: List<AccountEntry> = accountingService.fetchEntries(account.getIdentifier(), findCurrentEntries, Sort.Direction.ASC.name)
                        val balanceHolder: BalanceHolder
                        balanceHolder = if (currentAccountEntries.isEmpty()) {
                            BalanceHolder(BigDecimal.valueOf(account.getBalance()))
                        } else {
                            val accountEntry: AccountEntry = currentAccountEntries[0]
                            BalanceHolder(BigDecimal.valueOf(accountEntry.getBalance()).subtract(BigDecimal.valueOf(accountEntry.getAmount())))
                        }
                        val dividendHolder = DividendHolder()
                        dateRanges.forEach(Consumer { dateRange: String? ->
                            val accountEntries: List<AccountEntry> = accountingService.fetchEntries(account.getIdentifier(), dateRange, Sort.Direction.DESC.name)
                            if (accountEntries.isNotEmpty()) {
                                balanceHolder.balance = BigDecimal.valueOf(accountEntries[0].getBalance())
                            }
                            val currentBalance: BigDecimal = balanceHolder.balance
                            dividendHolder.addAmount(
                                accruedInterest(currentBalance, rate, 12, event.dueDate.lengthOfYear())
                            )
                        })
                        if (dividendHolder.amount > BigDecimal.ZERO) {
                            val roundedAmount: String = dividendHolder.amount
                                .setScale(2, BigDecimal.ROUND_HALF_EVEN).toString()
                            val cashToExpenseJournalEntry = JournalEntry()
                            cashToExpenseJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32))
                            cashToExpenseJournalEntry.setTransactionDate(DateConverter.toIsoString(now))
                            cashToExpenseJournalEntry.setTransactionType("INTR")
                            cashToExpenseJournalEntry.setClerk(UserContextHolder.checkedGetUser())
                            cashToExpenseJournalEntry.setNote("Dividend distribution.")
                            val cashDebtor = Debtor()
                            cashDebtor.setAccountNumber(productDefinitionView.cashAccountIdentifier)
                            cashDebtor.setAmount(roundedAmount)
                            cashToExpenseJournalEntry.setDebtors(Sets.newHashSet(cashDebtor))
                            val expenseCreditor = Creditor()
                            expenseCreditor.setAccountNumber(productDefinitionView.expenseAccountIdentifier)
                            expenseCreditor.setAmount(roundedAmount)
                            cashToExpenseJournalEntry.setCreditors(Sets.newHashSet(expenseCreditor))
                            accountingService.post(cashToExpenseJournalEntry)
                            payoutInterest(
                                productDefinitionView.expenseAccountIdentifier!!,
                                account.getIdentifier(),
                                roundedAmount
                            )
                        }
                    }
                }
            }
            val dividendDistributionView = DividendDistributionView()
            dividendDistributionView.productDefinition = productDefinitionView
            dividendDistributionView.dueDate = event.dueDate
            dividendDistributionView.rate = event.rate
            dividendDistributionView.createdOn = LocalDateTime.now(Clock.systemUTC())
            dividendDistributionView.createdBy = UserContextHolder.checkedGetUser()
            dividendDistributionViewRepository.save<DividendDistributionView>(dividendDistributionView)
        }
        return event.productDefinition
    }

    private fun periodOfInterestPayable(interestPayable: String): Int {
        return when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> 12
            InterestPayable.QUARTERLY -> 4
            else -> 1
        }
    }

    private fun shouldPayInterest(interestPayable: String, date: LocalDate): Boolean {
        return when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> date.equals(date.withDayOfMonth(date.lengthOfMonth()))
            InterestPayable.QUARTERLY -> date == YearQuarter.from(date).atEndOfQuarter()
            InterestPayable.ANNUALLY -> date.dayOfYear == date.lengthOfYear()
            else -> false
        }
    }

    private fun dateRanges(dueDate: LocalDate, interestPayable: String): List<String> {
        val pastDays: Int
        pastDays = when (InterestPayable.valueOf(interestPayable)) {
            InterestPayable.MONTHLY -> dueDate.lengthOfMonth()
            InterestPayable.QUARTERLY -> YearQuarter.from(dueDate).lengthOfQuarter()
            else -> dueDate.lengthOfYear()
        }
        return IntStream
            .range(1, pastDays)
            .mapToObj { value ->
                val before: LocalDate = dueDate.minusDays(value.toLong())
                DateConverter.toIsoString(before).toString() + ".." + DateConverter.toIsoString(dueDate.minusDays((value - 1).toLong()))
            }.collect(Collectors.toList())
    }

    private inner class BalanceHolder internal constructor(internal var balance: BigDecimal)
    private inner class DividendHolder internal constructor() {
        internal var amount: BigDecimal

        internal fun addAmount(toAdd: BigDecimal) {
            amount = amount.add(toAdd)
        }

        init {
            amount = BigDecimal.ZERO
        }
    }

    private fun accruableProduct(productDefinitionView: ProductDefinitionView): Boolean {
        return (productDefinitionView.active!! &&
            productDefinitionView.type!! != Type.SHARE &&
            productDefinitionView.interest != null && productDefinitionView.interest!! > 0.00)
    }

    private fun payoutInterest(expenseAccount: String, customerAccount: String, amount: String) {
        val expenseToCustomerJournalEntry = JournalEntry()
        expenseToCustomerJournalEntry.setTransactionIdentifier(RandomStringUtils.randomAlphanumeric(32))
        expenseToCustomerJournalEntry.setTransactionDate(DateConverter.toIsoString(LocalDateTime.now(Clock.systemUTC())))
        expenseToCustomerJournalEntry.setTransactionType("INTR")
        expenseToCustomerJournalEntry.setClerk(UserContextHolder.checkedGetUser())
        expenseToCustomerJournalEntry.setNote("Interest paid.")
        val expenseDebtor = Debtor()
        expenseDebtor.setAccountNumber(expenseAccount)
        expenseDebtor.setAmount(amount)
        expenseToCustomerJournalEntry.setDebtors(Sets.newHashSet(expenseDebtor))
        val customerCreditor = Creditor()
        customerCreditor.setAccountNumber(customerAccount)
        customerCreditor.setAmount(amount)
        expenseToCustomerJournalEntry.setCreditors(Sets.newHashSet(customerCreditor))
        accountingService.post(expenseToCustomerJournalEntry)
    }

    companion object {
        private const val INTEREST_PRECISION = 7
        private const val ACTIVE = "ACTIVE"

        /**
         * Copied from JavaMoney AnnualPercentageYield.calculate, and adjusted.
         * @return the resulting amount, never null.
         */
        private fun accruedInterest(
            amount: BigDecimal,
            rate: BigDecimal,
            periods: Int,
            lengthOfYear: Int
        ): BigDecimal {
            val baseFactor = rate.divide(BigDecimal.valueOf(periods.toLong()), MathContext.DECIMAL64)
                .add(BigDecimal.ONE)
            val annualInterest = amount.multiply(baseFactor.pow(periods).subtract(BigDecimal.ONE))
            return annualInterest
                .divide(BigDecimal.valueOf(lengthOfYear.toLong()),
                    amount.scale() + INTEREST_PRECISION, BigDecimal.ROUND_HALF_EVEN)
        }
    }
}
