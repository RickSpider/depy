select 
l.localidadid, 
l.localidad, 
dis.distritoid, 
dis.distrito, 
dep.departamentoid, 
dep.departamento  
from localidades l
left join distritos dis on dis.distritoid = l.distritoid
left join departamentos dep on dep.departamentoid = dis.departamentoid
order by l.localidadid asc
;