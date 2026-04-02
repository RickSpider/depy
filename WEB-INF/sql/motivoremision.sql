INSERT INTO public.tipos(
    creado, creadouser, modificacion, modificacionuser, descripcion, sigla, tipo, tipotipoid, codeextra)
VALUES 
(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado por venta', 'MOTIVOREMISION_TRASLADOVENTA', 'Traslado por venta', 9, 1),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado por consignación', 'MOTIVOREMISION_TRASLADOCONSIGNACION', 'Traslado por consignación', 9, 2),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Exportación', 'MOTIVOREMISION_EXPORTACION', 'Exportación', 9, 3),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado por compra', 'MOTIVOREMISION_TRASLADOCOMPRA', 'Traslado por compra', 9, 4),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Importación', 'MOTIVOREMISION_IMPORTACION', 'Importación', 9, 5),

(current_timestamp, 'admin', 'current_timestamp', 'admin', 'Traslado por devolución', 'MOTIVOREMISION_TRASLADODEVOLUCION', 'Traslado por devolución', 9, 6),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado entre locales de la empresa', 'MOTIVOREMISION_TRASLADOENTRELOCALES', 'Traslado entre locales de la empresa', 9, 7),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado de bienes por transformación', 'MOTIVOREMISION_TRANSFORMACION', 'Traslado de bienes por transformación', 9, 8),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado de bienes por reparación', 'MOTIVOREMISION_REPARACION', 'Traslado de bienes por reparación', 9, 9),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado por emisor móvil', 'MOTIVOREMISION_EMISORMOVIL', 'Traslado por emisor móvil', 9, 10),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Exhibición o demostración', 'MOTIVOREMISION_EXHIBICION', 'Exhibición o demostración', 9, 11),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Participación en ferias', 'MOTIVOREMISION_FERIAS', 'Participación en ferias', 9, 12),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Traslado de encomienda', 'MOTIVOREMISION_ENCOMIENDA', 'Traslado de encomienda', 9, 13),

(current_timestamp, 'admin', current_timestamp, 'admin', 'Decomiso', 'MOTIVOREMISION_DECOMISO', 'Decomiso', 9, 14);