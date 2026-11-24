# Mão Amiga — Apoio Comunitário (Global Solution 2026 · CitizenTech)

> *"Sua comunidade ajudando quem precisa."*

## Equipe
| Nome completo | RM |
|---|---|
| Nicole Alves Nogueira | 555182 |

## Problema
Em bairros e comunidades, pedidos de ajuda — alimentos, roupas, remédios, transporte, apoio a idosos, pequenos reparos — circulam de forma dispersa em grupos de mensagem, se perdem, não têm status e ninguém sabe o que já foi atendido. Isso gera duplicidade de esforços de um lado e pessoas sem atendimento do outro.

## Público beneficiado
- Moradores em situação de vulnerabilidade: idosos, famílias de baixa renda e pessoas com deficiência;
- Voluntários que querem ajudar de forma organizada;
- Líderes e associações de bairro que coordenam a ajuda local.

## Proposta da solução
O **Mão Amiga** é um aplicativo Android simples e offline para **registrar, acompanhar e organizar pedidos de ajuda** da comunidade. Cada pedido tem categoria, solicitante, contato, bairro e um **status** (Aberto → Em andamento → Atendido). É possível **favoritar** pedidos para priorizá-los, **editar**, **excluir** (com confirmação) e anexar opcionalmente a **localização** do pedido.

> **Aviso:** protótipo acadêmico desenvolvido para a Global Solution FIAP 2026. Os dados ficam armazenados somente no dispositivo; o app **não** possui integração com serviços públicos/governamentais nem com servidores externos.

## ODS relacionados
- **ODS 11 – Cidades e Comunidades Sustentáveis:** organiza a ajuda mútua no bairro, dando transparência ao que está aberto, em andamento e atendido; fortalece a participação e a gestão comunitária inclusiva (meta 11.3).
- **ODS 10 – Redução das Desigualdades:** dá visibilidade e prioridade (favoritos/status) às necessidades de idosos, pessoas com deficiência e famílias de baixa renda, incluindo-os na rede de apoio local.
- **ODS 1 – Erradicação da Pobreza:** facilita o acesso a recursos básicos (alimentos, roupas, remédios, transporte) por meio de uma rede solidária organizada, reduzindo pedidos esquecidos ou duplicados.

## Funcionalidades
- Tela inicial com contexto e contadores por status
- Cadastro de pedidos com validação de campos obrigatórios
- Listagem com filtro por status e estado vazio
- Detalhe do pedido
- Edição, exclusão (com confirmação), alteração de status e favoritos
- Localização opcional do pedido (GPS) e abertura no mapa
- Persistência local com Room (dados mantidos após reabrir o app)

## Tecnologias utilizadas
Kotlin 2.1 · Jetpack Compose (Material 3) · Navigation Compose · ViewModel + StateFlow · Room (KSP) · Coroutines/Flow · Google Play Services Location · Arquitetura MVVM

## Estrutura do projeto
```
app/src/main/java/br/com/fiap/maoamiga/
├── MaoAmigaApplication.kt      # cria o AppContainer (injeção manual)
├── MainActivity.kt             # setContent { MaoAmigaTheme { MaoAmigaApp() } }
├── di/AppContainer.kt          # instancia banco, repositório e LocationHelper (lazy)
├── data/
│   ├── local/                  # Room: Entity, DAO, Converters, Database
│   ├── model/                  # enums Categoria e StatusPedido
│   └── repository/             # PedidoAjudaRepository (fonte única de dados)
├── location/LocationHelper.kt  # localização atual via FusedLocationProvider (opcional)
├── ui/
│   ├── navigation/             # Rotas e NavHost (Navigation Compose)
│   ├── screens/                # home, lista, formulario, detalhe, sobre
│   ├── components/             # PedidoCard, StatusChip, EstadoVazio, formatadores
│   └── theme/                  # cores, tipografia e tema Material 3
└── viewmodel/                  # PedidosViewModel, FormularioViewModel, DetalheViewModel, Factory
```
- **data**: persistência com Room; o DAO expõe `Flow` para leitura e funções `suspend` para escrita.
- **viewmodel**: estado da UI exposto como `StateFlow` (`stateIn`), validação e eventos; nenhuma tela acessa DAO/Repository diretamente.
- **ui**: composables sem regra de negócio, que coletam o estado com `collectAsStateWithLifecycle()`.

## Como executar
1. Clonar/extrair o projeto e abrir a pasta raiz no Android Studio.
2. Aguardar o Gradle Sync (JDK 17 ou JBR do Android Studio).
3. Criar/selecionar um emulador (API 26+; recomendado API 34/35 com Google APIs).
4. Clicar em Run ▶ (módulo `app`).
5. (Opcional) Para testar localização no emulador: Extended Controls > Location > Set location.

## Fluxo principal
Home → Novo pedido → Salvar → Lista → Detalhe → Alterar status/Favoritar/Editar/Excluir.

## Evidências (capturas de tela reais)
| Etapa | Imagem |
|---|---|
| Tela inicial | ![](docs/screenshots/01_home.png) |
| Lista vazia | ![](docs/screenshots/02_lista_vazia.png) |
| Validação do formulário | ![](docs/screenshots/03_validacao.png) |
| Criação de registro | ![](docs/screenshots/04_cadastro.png) |
| Registro na lista | ![](docs/screenshots/05_lista_com_registro.png) |
| Ação adicional (status/favorito) | ![](docs/screenshots/06_acao_adicional.png) |
| Edição | ![](docs/screenshots/07_edicao.png) |
| Exclusão (confirmação) | ![](docs/screenshots/08_exclusao.png) |
| Persistência após reabrir o app | ![](docs/screenshots/09_persistencia.png) |
| Tela Sobre / ODS | ![](docs/screenshots/10_sobre.png) |
