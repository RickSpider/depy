select 
remisionid, 
cdc
from remisiones
where cdc is not null 
and estado like '%Pendiente%'
-- and empresaid = '?1'
;