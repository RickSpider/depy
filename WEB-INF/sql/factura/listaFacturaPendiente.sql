select 
facturaid, 
cdc
from facturas
where cdc is not null 
and estado like '%Pendiente%'
-- and empresaid = '?1'
;