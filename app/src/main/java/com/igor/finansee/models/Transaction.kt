package com.igor.finansee.models

import java.util.UUID // Para gerar IDs únicos

data class Transaction(
    val id: String = UUID.randomUUID().toString(), // ID único para a transação
    val userId: String, // ID do usuário que registrou a transação

    val value: Double, // Valor da transação (sempre positivo, o tipo define se é entrada/saída)
    val description: String, // Descrição da transação (ex: "Almoço", "Salário", "Transferência para poupança")
    val date: Long, // Timestamp da data da transação em milissegundos (para facilitar ordenação e filtros)

    val type: TransactionType, // Enum para o tipo de transação (Receita, Despesa, Transferencia)

    // OBRIGATÓRIOS para a maioria das transações
    val accountId: String, // ID da conta de origem (para despesa/transferência) ou destino (para receita/transferência)

    // OPCIONAIS dependendo do tipo da transação
    val destinationAccountId: String?, // ID da conta de destino (somente para transferências)
    val creditCardId: String?, // ID do cartão de crédito (somente para despesas de cartão)
    val categoryId: String?, // ID da categoria (para receitas/despesas)
    val subcategoryId: String?, // ID da subcategoria (para receitas/despesas)

    // Para controle de status e recorrência
    val isPaid: Boolean = true, // Indica se a transação já foi efetivada (true) ou está pendente (false - contas a pagar/receber)
    val isRecurring: Boolean = false, // Indica se a transação é parte de uma série recorrente
    val isInstallment: Boolean = false, // Indica se a transação é parcelada
    val currentInstallment: Int = 1, // Número da parcela atual (ex: 1 de 3)
    val totalInstallments: Int = 1, // Número total de parcelas (ex: 3)
    val originalTransactionId: String? // ID da transação "mãe" para agrupar parcelas ou recorrências
)

// Enum para os tipos de transação, melhor do que String para tipagem segura
enum class TransactionType {
    INCOME,        // Receita
    EXPENSE,       // Despesa
    TRANSFER,      // Transferência entre contas
    CREDIT_CARD_EXPENSE // Despesa no cartão de crédito
}