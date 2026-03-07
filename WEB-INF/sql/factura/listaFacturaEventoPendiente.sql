Select f.facturaid, f.cdc, f.eventoid, f.eventoestado, te.tipo
from facturas f
left join tipos te on te.tipoid = f.eventotipoid
where f.eventoestado like '%Pendiente%'
-- and empresaid = '?1'
order by te.tipo asc;