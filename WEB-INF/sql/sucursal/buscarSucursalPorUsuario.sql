Select su.sucursalusuarioid as ID, s.nombre as Nombre, s.establecimiento as Establecimiento
from sucursalesusuarios su
join sucursales s on s.sucursalid = su.sucursalid
where su.empresaid = ?1
and su.usuarioid = ?2
order by su.sucursalid asc;