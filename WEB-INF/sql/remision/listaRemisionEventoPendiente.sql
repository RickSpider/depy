Select r.remisionid, r.cdc, r.eventoid, r.eventoestado, te.tipo
from remisiones r
left join tipos te on te.tipoid = r.eventotipoid
where r.eventoestado like '%Pendiente%'
-- and empresaid = '?1'
order by te.tipo asc;