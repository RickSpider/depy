Select s.sucursalid as ID, s.nombre as Nombre, s.establecimiento as Establecimiento
from sucursales s
where s.empresaid = ?1
order by s.sucursalid;