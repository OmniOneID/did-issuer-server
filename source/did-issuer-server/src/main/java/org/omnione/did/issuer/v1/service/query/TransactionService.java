/*
 * Copyright 2024 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.constant.SubTransactionStatus;
import org.omnione.did.base.db.constant.SubTransactionType;
import org.omnione.did.base.db.constant.TransactionStatus;
import org.omnione.did.base.db.domain.SubTransaction;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.base.db.repository.SubTransactionRepository;
import org.omnione.did.base.db.repository.TransactionRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.ValidationUtil;
import org.springframework.stereotype.Service;

/**
 *
 * Handles transaction and sub-transaction management, including creation,
 * retrieval, status updates, and expiration handling within the DID system.
 */
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final SubTransactionRepository subTransactionRepository;

    /**
     * Finds a transaction by its transaction ID.
     *
     * @param txId Transaction ID to search for.
     * @return Found transaction.
     * @throws OpenDidException if the transaction is not found.
     */
    public Transaction findByTxId(String txId) {
        return transactionRepository.findByTxId(txId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    /**
     * Inserts a new transaction into the repository.
     *
     * @param transaction Transaction to be saved.
     * @return Saved transaction.
     */
    public Transaction insertTransaction(Transaction transaction) {
        /**
         * The save method is called on the transactionRepository object to save the transaction to the database.
         */
        return transactionRepository.save(transaction);
    }


    /**
     * Updates the status of a transaction.
     *
     * @param transaction Transaction to update.
     * @param status      New status of the transaction.
     * @return Updated transaction.
     */
    public Transaction updateTransactionStatus(Transaction transaction, TransactionStatus status) {
        transaction.setStatus(status);
        return transactionRepository.save(transaction);
    }

    /**
     * Inserts a new sub-transaction into the repository.
     *
     * @param subTransaction Sub-transaction to be saved.
     * @return Saved sub-transaction.
     */
    public SubTransaction insertSubTransaction(SubTransaction subTransaction) {

        return subTransactionRepository.save(subTransaction);
    }

    /**
     * Finds the last sub-transaction for a given transaction ID.
     *
     * @param transactionId Transaction ID.
     * @return Last sub-transaction.
     * @throws OpenDidException if the sub-transaction is not found.
     */
    public SubTransaction findByTransactionIdOrderByStepDesc(Long transactionId) {
        return subTransactionRepository.findFirstByTransactionIdOrderByStepDesc(transactionId)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    /**
     * Retrieves the expiration time of a transaction.
     *
     * @return Transaction expiration time.
     */
    public Transaction validateAndFindTransaction(String txId, SubTransactionType subTransactionType) {
        Transaction transaction = findByTxId(txId);
        validateTransaction(transaction, subTransactionType);
        return transaction;
    }

    /**
     * Validates the transaction and sub-transaction.
     *
     * @param transaction       Transaction to validate.
     * @param type Sub-transaction type to validate.
     * @throws OpenDidException if the transaction is invalid.
     * @throws OpenDidException if the transaction is expired.
     * @throws OpenDidException if the sub-transaction is invalid.
     */
    public void validateTransaction(Transaction transaction, SubTransactionType type) {
        if (ValidationUtil.isExpiredDate(transaction.getExpiredAt())) {
            updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.TRANSACTION_EXPIRED);
        }

        if (!TransactionStatus.COMPLETED.equals(transaction.getStatus())) {
            updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }

        SubTransaction subTransaction = findByTransactionIdOrderByStepDesc(transaction.getId());
        if (!(subTransaction.getType() == type)
                || !SubTransactionStatus.COMPLETED.equals(subTransaction.getStatus())) {
            updateTransactionStatus(transaction, TransactionStatus.FAILED);
            throw new OpenDidException(ErrorCode.TRANSACTION_INVALID);
        }
    }

    public Transaction findById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.TRANSACTION_NOT_FOUND));
    }
}
