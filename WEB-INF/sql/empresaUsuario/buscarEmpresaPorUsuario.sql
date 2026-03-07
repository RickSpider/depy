Select eu.empresausuarioid as id, e.razonsocial as empresa, e.ruc as ruc from empresasusuarios eu
join empresas e on e.empresaid = eu.empresaid
where usuarioid = ?1
order by empresausuarioid asc;