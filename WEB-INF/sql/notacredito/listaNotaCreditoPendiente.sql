select 
notacreditoid, 
cdc
from notascreditos
where cdc is not null 
and estado like '%Pendiente%'
-- and empresaid = '?1'
;