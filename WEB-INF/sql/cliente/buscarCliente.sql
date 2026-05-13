SELECT
clienteid,
documentoNro,
razonsocial,
direccion,
empresaid
FROM clientes
where empresaid = ?1

UNION ALL

SELECT
clienteid,
documentoNro,
razonsocial,
direccion,
empresaid
FROM clientes
where empresaid is null
order by empresaid desc, clienteid asc;