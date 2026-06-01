package com.stamos.payment;

import com.stamos.entity.Pago;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Interfaz que define los métodos para integrar diferentes pasarelas de pago.
 * Actualmente se usa una implementación manual (transferencia bancaria / Yape).
 * En el futuro se pueden agregar implementaciones para Culqi, Izipay, PayPal, etc.
 */
public interface PaymentGateway {

    /**
     * Inicia un proceso de pago (solicitud de pago).
     * @param pago Datos del pago
     * @return Mapa con información de la transacción (URL, ID, etc.)
     */
    Map<String, Object> iniciarPago(Pago pago);

    /**
     * Verifica el estado de un pago después de que el cliente realizó la transferencia.
     * En modo manual, esta verificación la hace el administrativo.
     * @param pagoId Identificador del pago
     * @return true si se confirma el pago, false en caso contrario
     */
    boolean verificarPago(Long pagoId);

    /**
     * Obtiene las instrucciones de pago (datos bancarios, QR Yape) para mostrar al cliente.
     * @return Mapa con instrucciones (banco, cuenta, CCI, Yape, etc.)
     */
    Map<String, String> obtenerInstruccionesPago();
}