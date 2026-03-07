Select u.usuarioid as id, u.account as usuario, u.fullname as Nombre from empresasusuarios eu
join usuarios u on u.usuarioid = eu.usuarioid
where empresaid = ?1;