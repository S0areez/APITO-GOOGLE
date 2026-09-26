# 🏟️ Apito — Marketplace de Árbitros (Árbitro de Aluguel 2.0)

Aplicativo Android nativo (Kotlin + Jetpack Compose) construído a partir do projeto **Árbitro de Aluguel 2.0**.
Conecta organizadores de partidas amadoras e ligas a árbitros qualificados para Futebol de Campo, Futsal, Society e Futebol 7.

---

## 🚀 Funcionalidades Portadas

### 👤 Seleção de Papel & Autenticação
- **Tela Inicial / Splash**: Apresentação visual premium com estética noturna de estádio, iluminação cênica e cartões "Sou Contratante" e "Sou Árbitro".
- **Alternância Dinâmica de Perfil**: Alterne entre a visão do Contratante e do Árbitro a qualquer momento pelo cabeçalho ou menu de Perfil para testes completos.

### 📱 Experiência do Contratante
1. **Dashboard do Contratante**:
   - Saudação personalizada, busca rápida de juízes e banner de acesso à Gestão de Ligas.
   - Lista das próximas partidas com status ao vivo.
   - Carrossel com árbitros recomendados e estrelas de avaliação.
2. **Busca Avançada de Árbitros**:
   - Filtro em tempo real por nome e cidade.
   - Filtros por modalidade esportiva (Futebol Campo, Futsal, Society, Futebol 7).
   - Filtros por equipamentos obrigatórios (Apito, Cartões, Cronômetro, Súmula, Placar).
   - Slider de preço máximo por partida (R$ 80 a R$ 300).
3. **Perfil Completo do Árbitro**:
   - Foto/avatar com insígnia de nível (🥉 Bronze, 🥈 Prata, 🥇 Ouro, 🏆 Black).
   - Estatísticas de partidas apitadas, 98% pontualidade e média de cartões por jogo.
   - Modalidades credenciadas, equipamentos próprios e dias de disponibilidade.
   - Histórico de avaliações com comentários de outros contratantes.
   - Botão de contratação direta com valor por jogo.
4. **Checkout Completo & Split de Pagamento**:
   - Seleção de data, horário, arena/local e duração da partida.
   - Cálculo automático do valor total, taxa de plataforma (10%) e acréscimo para horário nobre (+20%).
   - **Calculadora de Rateio**: Divida o valor do jogo entre os atletas do time (ex: R$ 15,00 por jogador).
   - Métodos de pagamento: PIX, Cartão e Saldo da Carteira.
5. **Tela de Pagamento PIX**:
   - Exibição de QR Code e código PIX Copia e Cola funcional.
   - Botão para **Simular Pagamento Aprovado** e iniciar o modo partida instantaneamente.
6. **Gestão de Ligas e Torneios**:
   - Importação em lote de tabelas de jogos (CSV).
   - Algoritmo inteligente de escalação automática de árbitros conforme a modalidade e local.

### ⚽ Experiência do Árbitro
1. **Dashboard do Árbitro**:
   - Resumo financeiro de ganhos na semana (recebidos e a receber).
   - Alternador de disponibilidade (Disponível / Indisponível para novas partidas).
   - Alerta destacado de novas solicitações pendentes.
   - Card hero com atalho para a próxima partida agendada.
2. **Solicitações de Partidas**:
   - Visualização de convites recebidos com horário, local e valor do jogo.
   - Botões para **Aceitar** ou **Recusar** partidas em 1 toque.
3. **Modo Partida (Match Day Mode)**:
   - **Check-in Duplo**: Contratante e Árbitro confirmam presença na arena esportiva.
   - **Cronômetro Digital ao Vivo**: Iniciar, pausar e finalizar tempo de jogo.
   - **Alerta de Tempo Extra**: Notificação automática caso a partida ultrapasse a duração contratada (+10min).
   - **Súmula Digital ao Vivo**: Registro imediato de ocorrências no jogo (⚽ Gol, 🟨 Cartão Amarelo, 🟥 Cartão Vermelho, ⚠️ Falta) com minuto e descrição.
   - **Telemetria GPS**: Status de localização e acompanhamento de trajeto.
   - **Avaliação Pós-Jogo**: Avaliação mútua com estrelas (1 a 5), pontualidade, profissionalismo e feedback.

### 💼 Carteira & Perfil
1. **Carteira**:
   - Saldo disponível com dados em tempo real.
   - Solicitação de saque via chave PIX (CPF, Email ou Telefone).
   - Extrato completo de movimentações (entradas por jogos apitados, saques e pagamentos).
2. **Perfil do Usuário**:
   - Edição de dados cadastrais (nome, WhatsApp, cidade, biografia, valor/hora).
   - Alternância rápida de papel e logout.

---

## 🛠️ Tecnologias Utilizadas
- **Linguagem**: Kotlin 2.2
- **Interface**: Jetpack Compose com Material Design 3
- **Persistência**: Android Room Database com DAOs reativos (Flow)
- **Imagens e Ícones**: Coil Compose & Custom Adaptive Launcher Icon
- **Arquitetura**: MVVM com Clean Architecture e StateFlow
