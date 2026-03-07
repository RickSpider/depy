SELECT
clienteid,
documentoNro,
razonsocial,
direccion
FROM clientes
where empresaid = ?1
order by clienteid asc;