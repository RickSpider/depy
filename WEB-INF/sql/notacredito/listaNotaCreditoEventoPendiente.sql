Select nc.notacreditoid, nc.cdc, nc.eventoid, nc.eventoestado, te.tipo
from notascreditos nc
left join tipos te on te.tipoid = nc.eventotipoid
where nc.eventoestado like '%Pendiente%'
-- and nc.empresaid = '?1'
order by te.tipo asc;