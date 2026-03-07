Select
c.comprobanteid,
ct.tipo,
c.timbrado,
c.establecimiento,
c.puntoexpedicion,
c.activo
from comprobantes c
left join tipos ct on ct.tipoid = c.comprobantetipoid
where empresaid = ?1
order by comprobanteid asc;