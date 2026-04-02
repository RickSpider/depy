select  
r.remisionid,
TO_CHAR(r.fecha, 'dd-mm-yyyy HH24:MI:SS') as fecha, 
r.timbradodocnro, 
r.cdc,
r.documentonro, 
r.razonsocial, 
r.estado, 
r.qr,
te.tipo,
TO_CHAR(r.eventofecha, 'dd-mm-yyyy HH24:MI:SS') as eventofecha,
r.eventoEstado

from remisiones r
left join tipos te on te.tipoid = r.eventotipoid
where r.empresaid = ?1 
--1 and r.sucursalid = ?2 
--2 and r.fecha between '?3' and '?4' 
--3 and r.estado like '%Aprobado%' and r.eventotipoid isnull
order by r.remisionid desc;