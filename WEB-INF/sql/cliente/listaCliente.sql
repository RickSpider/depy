SELECT
clienteid,
documentoNro,
razonsocial,
email,
celular,
telefono
FROM clientes
where empresaid = ?1
order by clienteid asc;