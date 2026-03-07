SELECT eu.empresausuarioid, u.account, u.fullname,u.activo 
from empresasusuarios eu
join empresas e on e.empresaid = eu.empresaid
join usuarios u on u.usuarioid = eu.usuarioid
where eu.empresaid = ?1
order by eu.empresausuarioid asc;