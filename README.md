# CoreJavaMarch2017
WITH transformed_data AS (
    SELECT
        -- Trim and append "pi_type_cd"
        CONCAT('pi_type_cd', TRIM(pi_type_cd)) AS dg_pi_type_cd,

        -- Initialize ptc_first_2_bytes as empty string
        '' AS ptc_first_2_bytes,

        -- Vector search equivalent: Checking if transaction_code exists in the given list
        CASE 
            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
            THEN 1 
            ELSE -1 
        END AS index_vec,

        -- Determine Credit (C) or Debit (D) based on transaction amount
        CASE 
            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                AND SUBSTRING(TRIM(transaction_amount), 1, 1) = '-' THEN 'C'
            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                AND SUBSTRING(TRIM(transaction_amount), 1, 1) != '-' THEN 'D'
            ELSE NULL 
        END AS trxn_cr_dbt_1,

        -- Trim and append "trxn_cr_dbt"
        CONCAT('trxn_cr_dbt', TRIM(transaction_code)) AS trxn_cr_dbt_2,

        -- First defined value of trxn_cr_dbt_1 and trxn_cr_dbt_2
        COALESCE(
            CASE 
                WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                    AND SUBSTRING(TRIM(transaction_amount), 1, 1) = '-' THEN 'C'
                WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                    AND SUBSTRING(TRIM(transaction_amount), 1, 1) != '-' THEN 'D'
                ELSE NULL 
            END,
            CONCAT('trxn_cr_dbt', TRIM(transaction_code))
        ) AS trxn_cr_dbt,

        -- Compute v_posted_trxn_cd
        CASE 
            WHEN TRIM(transaction_code) = '271' AND no_post_reason > 0 THEN
                CONCAT(
                    '',
                    LPAD(CAST(no_post_reason AS STRING), 6, '0'),
                    COALESCE(
                        CASE 
                            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                                AND SUBSTRING(TRIM(transaction_amount), 1, 1) = '-' THEN 'C'
                            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                                AND SUBSTRING(TRIM(transaction_amount), 1, 1) != '-' THEN 'D'
                            ELSE NULL 
                        END,
                        CONCAT('trxn_cr_dbt', TRIM(transaction_code))
                    ),
                    CONCAT('pi_type_cd', TRIM(pi_type_cd))
                )
            ELSE
                CONCAT(
                    '',
                    LPAD(TRIM(transaction_code), 6, '0'),
                    COALESCE(
                        CASE 
                            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                                AND SUBSTRING(TRIM(transaction_amount), 1, 1) = '-' THEN 'C'
                            WHEN transaction_code IN ('259', '260', '261', '273', '280', '285', '290', '370', '400')
                                AND SUBSTRING(TRIM(transaction_amount), 1, 1) != '-' THEN 'D'
                            ELSE NULL 
                        END,
                        CONCAT('trxn_cr_dbt', TRIM(transaction_code))
                    ),
                    CONCAT('pi_type_cd', TRIM(pi_type_cd))
                )
        END AS v_posted_trxn_cd
    FROM my_table
)
-- Lookup transaction_type_key from fsc_wells_trans_dim_lkp
SELECT 
    COALESCE(lkp.transaction_type_key, '') AS transaction_type_key
FROM transformed_data t
LEFT JOIN fsc_wells_trans_dim_lkp lkp 
    ON lkp.lookup_column = 'FDRCOB' 
    AND lkp.lookup_value = t.v_posted_trxn_cd;
