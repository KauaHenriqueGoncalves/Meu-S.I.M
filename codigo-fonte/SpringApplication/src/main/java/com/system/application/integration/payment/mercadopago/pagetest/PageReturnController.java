package com.system.application.integration.payment.mercadopago.pagetest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth/payment")
public class PageReturnController {

    @GetMapping("/success")
    public ResponseEntity<String> success(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(buildPage(
                        "✅ Pagamento Aprovado!",
                        "Sua licença foi ativada com sucesso.",
                        "#28a745",
                        payment_id,
                        external_reference
                ));
    }

    @GetMapping("/pending")
    public ResponseEntity<String> pending(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(buildPage(
                        "⏳ Pagamento Pendente",
                        "Aguardando confirmação do pagamento.",
                        "#ffc107",
                        payment_id,
                        external_reference
                ));
    }

    @GetMapping("/failure")
    public ResponseEntity<String> failure(
            @RequestParam(required = false) String payment_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String external_reference
    ) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(buildPage(
                        "❌ Pagamento Recusado",
                        "Não foi possível processar seu pagamento. Tente novamente.",
                        "#dc3545",
                        payment_id,
                        external_reference
                ));
    }

    private String buildPage(
            String title,
            String message,
            String color,
            String paymentId,
            String externalReference
    ) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Focus Gestão - Pagamento</title>
                    <style>
                        * { margin: 0; padding: 0; box-sizing: border-box; }
                        body {
                            font-family: Arial, sans-serif;
                            background: #f5f5f5;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                        }
                        .card {
                            background: white;
                            border-radius: 12px;
                            padding: 48px 40px;
                            text-align: center;
                            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                            max-width: 420px;
                            width: 100%%;
                        }
                        .badge {
                            display: inline-block;
                            background: %s;
                            color: white;
                            border-radius: 50px;
                            padding: 8px 24px;
                            font-size: 14px;
                            font-weight: bold;
                            margin-bottom: 24px;
                        }
                        h1 {
                            font-size: 22px;
                            color: #333;
                            margin-bottom: 12px;
                        }
                        p {
                            color: #666;
                            font-size: 15px;
                            margin-bottom: 32px;
                        }
                        .info {
                            background: #f9f9f9;
                            border-radius: 8px;
                            padding: 16px;
                            text-align: left;
                            font-size: 13px;
                            color: #555;
                        }
                        .info span {
                            display: block;
                            margin-bottom: 6px;
                        }
                        .info b { color: #333; }
                        .footer {
                            margin-top: 32px;
                            font-size: 12px;
                            color: #aaa;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <div class="badge">Focus Gestão</div>
                        <h1>%s</h1>
                        <p>%s</p>
                        <div class="info">
                            <span><b>Payment ID:</b> %s</span>
                            <span><b>Referência:</b> %s</span>
                        </div>
                        <div class="footer">Em caso de dúvidas, entre em contato com o suporte.</div>
                    </div>
                </body>
                </html>
                """.formatted(color, title, message, paymentId, externalReference);
    }

}
