# CoreJavaMarch2017
-- Lookup transaction_type_key from fsc_wells_trans_dim_lkp
SELECT 
    COALESCE(lkp.transaction_type_key, '') AS transaction_type_key
FROM transformed_data t
LEFT JOIN fsc_wells_trans_dim_lkp lkp 
    ON lkp.lookup_column = 'FDRCOB' 
    AND lkp.lookup_value = t.v_posted_trxn_cd;
