select  
f.facturaid,
TO_CHAR(f.fecha, 'dd-mm-yyyy HH24:MI:SS') as fecha, 
f.timbradodocnro, 
f.cdc,
tc.tipo, 
f.documentonro, 
f.razonsocial, 
f.estado, 
coalesce(f.totaldetalle,0),
f.qr,
te.tipo,
TO_CHAR(f.eventofecha, 'dd-mm-yyyy HH24:MI:SS') as eventofecha,
f.eventoEstado,
tm.tipo,
f.monedaCambio
from facturas f
left join tipos tc on tc.tipoid = f.condicionid
left join tipos te on te.tipoid = f.eventotipoid
left join tipos tm on tm.tipoid = f.monedaid
where f.empresaid = ?1 
--1 and f.sucursalid = ?2 
--2 and f.fecha between '?3' and '?4' 
--3 and estado like '%Aprobado%' and f.eventotipoid isnull
order by facturaid desc;