select  
nc.notacreditoid,
TO_CHAR(nc.fecha, 'dd-mm-yyyy HH24:MI:SS') as fecha, 
nc.timbradodocnro, 
nc.cdc,
nc.documentonro, 
nc.razonsocial, 
nc.estado, 
coalesce(nc.totaldetalle,0),
nc.qr,
te.tipo,
TO_CHAR(nc.eventofecha, 'dd-mm-yyyy HH24:MI:SS') as eventoncecha,
nc.eventoEstado,
tm.tipo,
nc.monedaCambio
from notascreditos nc
left join tipos te on te.tipoid = nc.eventotipoid
left join tipos tm on tm.tipoid = nc.monedaid
where nc.empresaid = ?1 
--1 and nc.sucursalid = ?2 
--2 and nc.fecha between '?3' and '?4' 
--3 and estado like '%Aprobado%' and nc.eventotipoid isnull
order by notacreditoid desc;