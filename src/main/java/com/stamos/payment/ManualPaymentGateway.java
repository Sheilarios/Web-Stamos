package com.stamos.payment;

import com.stamos.entity.Pago;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación manual del gateway de pagos.
 * Muestra datos bancarios y QR de Yape, y permite la confirmación manual por parte del administrativo.
 * No realiza comunicación con ninguna pasarela externa.
 */
@Component
public class ManualPaymentGateway implements PaymentGateway {

    @Override
    public Map<String, Object> iniciarPago(Pago pago) {
        // En modo manual, solo se registra la intención de pago.
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("estado", "PENDIENTE");
        resultado.put("mensaje", "Por favor realice la transferencia a los siguientes datos bancarios y suba el comprobante.");
        resultado.put("instrucciones", obtenerInstruccionesPago());
        return resultado;
    }

    @Override
    public boolean verificarPago(Long pagoId) {
        // En modo manual, la verificación la realiza el administrativo.
        // Este método se llamaría cuando el administrativo confirma manualmente el pago.
        // Por defecto retorna false; el administrativo actualizará el estado.
        return false;
    }

    @Override
    public Map<String, String> obtenerInstruccionesPago() {
        Map<String, String> instrucciones = new HashMap<>();
        instrucciones.put("banco", "BCP");
        instrucciones.put("numero_cuenta", "123-456-789");
        instrucciones.put("cci", "00212345678901234567");
        instrucciones.put("titular", "STAMOS LINARES S.R.L.");
        instrucciones.put("yape_qr_url", "/images/yape_qr.png");
        instrucciones.put("mensaje", "Realiza tu transferencia o pago por Yape y luego sube el comprobante. El pago será verificado manualmente en un plazo máximo de 24 horas.");
        return instrucciones;
    }
}