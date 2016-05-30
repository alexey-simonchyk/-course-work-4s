package view;


import controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Card;
import model.server.ServerPlayer;

import java.util.ArrayList;

public class Window extends Application {
    private final int CARD_HEIGHT = 86;
    private final int CARD_WIDTH = 58;
    private Image cards; // Крестя - 2 , Пика - 3 , Бубна - 1 , Чирва - 0
    private Image cardBack; // колода карт
    private HBox playerCards, firstEnemy;
    private VBox deck;
    private VBox tableCards;
    private Controller controller;
    private Stage stage;
    private TextArea chat;

    public Window() {
        cards = loadImage("cards.png");
        cardBack = loadImage("card_back.png");
    }

    public static void applicationBegin(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Fool online");
        this.stage = stage;

        setMainMenuScene();
        stage.show();
        controller = new Controller(this);
        stage.setOnCloseRequest(event -> controller.stopThreads());
    }

    private void setClientScene() {
        VBox serverScene = getVBox(50);
        Scene scene = new Scene(serverScene, 800, 600);
        Label ipLabel = new Label("Write ip server");
        TextField ipTextField = new TextField();
        Label portLabel = new Label("Write port to connect");
        TextField portTextField = new TextField();
        portTextField.setText("7070");
        ipTextField.setText("127.0.0.1");
        Button buttonConnect = new Button("Connect");
        serverScene.getChildren().addAll(ipLabel, ipTextField, portLabel, portTextField, buttonConnect);
        stage.setScene(scene);
        controller.setIsServer(false);

        buttonConnect.setOnAction(event -> {
            String ip = ipTextField.getText();
            String port = portTextField.getText();
            if (!ip.equals("") && !port.equals("")) {
                Label labelMessage = new Label("Connecting...");
                serverScene.getChildren().add(labelMessage);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        controller.connectToServer(ip, Integer.parseInt(port));
                    }
                }).start();
            }
        });
    }

    public void startGame() {
        Platform.runLater(() -> {
            setGameScene();
            controller.setInMainMenu(false);
            controller.updateView();
        });
    }

    public void updateChat(String message) {
        Platform.runLater(()-> chat.setText(chat.getText() + "\n" + message));
    }


    private void setServerScene() {
        VBox serverScene = getVBox(50);
        Scene scene = new Scene(serverScene, 800, 600);
        Label labelMessage = new Label("Waiting players...");
        serverScene.getChildren().add(labelMessage);
        stage.setScene(scene);
        controller.setIsServer(true);
        new Thread(new Runnable(){
            @Override
            public void run() {
                controller.playerWait();
            }
        }).start();
    }

    private void setMainMenuScene() {
        VBox mainMenu = getVBox(50);
        Scene scene = new Scene(mainMenu, 800, 600);
        Button startButton = getButton("Start server");
        Button connectButton = getButton("Connect");
        Button exitButton = getButton("Exit");
        mainMenu.getChildren().add(startButton);
        mainMenu.getChildren().add(connectButton);
        mainMenu.getChildren().add(exitButton);

        startButton.setOnAction(event->{
                setServerScene();
        });

        exitButton.setOnAction(event -> {
            controller.stopThreads();
            stage.close();
        });

        connectButton.setOnAction(event-> setClientScene());

        stage.setScene(scene);
    }

    private Button getButton(String name) {
        Button button = new Button(name);
        button.setMinWidth(250);
        button.setMinHeight(50);
        return button;
    }

    private void setGameScene() {
        playerCards = new HBox();
        tableCards = new VBox();
        deck = new VBox();
        firstEnemy = new HBox();
        VBox controlArea = getControlArea(10);

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-image: url(\"background.jpg\");");
        Scene scene = new Scene(borderPane, 800, 600);

        //КОЛОДА КАРТ
        borderPane.setRight(deck);
        deck.setMinWidth(CARD_HEIGHT);
        deck.setAlignment(Pos.CENTER_RIGHT);
        deck.setSpacing(-CARD_HEIGHT);

        //КАРТЫ ИГРОКА
        borderPane.setBottom(playerCards);
        playerCards.setSpacing(10);
        playerCards.setAlignment(Pos.CENTER);

        //ПОЛЕ ИГРАЛЬНОГО СТОЛА
        borderPane.setCenter(tableCards);
        tableCards.setSpacing(20);
        tableCards.setAlignment(Pos.CENTER);

        //КАРТЫ ПРОТИВНИКОВ
        borderPane.setTop(firstEnemy);
        firstEnemy.setAlignment(Pos.CENTER);

        borderPane.setLeft(controlArea);

        stage.setScene(scene);
    }

    public void update(ArrayList<Card> gameCards, ArrayList<Card> playerCards,
                       ArrayList<ServerPlayer> players, Card trump) { // обновление отображения карт на столе
        displayCardsPlayer(playerCards, this.playerCards);
        displayCardsTable(gameCards, tableCards);
        displayCardDeck(trump, deck);
        int temp = players.get(0).getNumberCards();
        displayEnemyCards(firstEnemy, temp, 0);
        firstEnemy.setSpacing(10 + ( -10 * ( temp / 3 ) ));

    }

    private VBox getControlArea(int spacing) {
        VBox newControlArea = new VBox();
        newControlArea.setSpacing(spacing);
        newControlArea.setAlignment(Pos.CENTER);
        Button buttonPass = getButton("Пасс");
        Button buttonTake = getButton("Взять");
        chat = new TextArea();
        chat.setEditable(false);
        chat.setPrefWidth(250);
        chat.setPrefHeight(400);
        HBox messageArea = getHBox(5);
        chat.setWrapText(true);
        messageArea.setPrefWidth(250);
        TextField messageText = new TextField();
        messageText.setPrefWidth(150);
        Button buttonSendMessage = new Button("Отправить");
        buttonSendMessage.setPrefWidth(95);
        messageArea.getChildren().addAll(messageText, buttonSendMessage);
        newControlArea.getChildren().addAll(buttonPass, buttonTake, chat, messageArea);
        buttonPass.setOnAction(event -> new Thread(new Runnable() {
            @Override
            public void run() {
                controller.controlButtonPressed(true);
            }
        }).start());
        buttonTake.setOnAction(event -> new Thread(new Runnable() {
            @Override
            public void run() {
                controller.controlButtonPressed(false);
            }
        }).start());
        buttonSendMessage.setOnAction(event->new Thread(new Runnable() {
            @Override
            public void run() {
                controller.buttonSendMessagePressed(messageText.getText());
                Platform.runLater(()->messageText.setText(""));
            }
        }).start());
        return newControlArea;
    }

    private void displayCardsTable(ArrayList<Card> cardsList, VBox node) { // отображение карт , которыми ходили
        Platform.runLater(() -> {
            node.getChildren().clear();
            HBox tempNode = null;

            int index = 0;
            for (Card card: cardsList) {
                if (index == 0) {
                    tempNode = getHBox(20);
                    node.getChildren().add(tempNode);
                    index = 0;
                }
                ImageViewCard view = new ImageViewCard(cards, card);
                view.setViewport(getCardSprite(card));
                if (index % 2 == 0) {
                    HBox temp = getHBox(-30);
                    temp.getChildren().add(view);
                    tempNode.getChildren().add(temp);
                } else {
                    HBox temp = (HBox)tempNode.getChildren().get(index / 2);
                    temp.getChildren().add(view);
                }
                if ( ++index == 6 ) {
                    index = 0;
                }
            }
        });
    }

    private HBox getHBox(int spacing) {
        HBox result = new HBox();
        result.setSpacing(spacing);
        result.setAlignment(Pos.CENTER);
        return result;
    }

    private VBox getVBox(int spacing) {
        VBox result = new VBox();
        result.setSpacing(spacing);
        result.setAlignment(Pos.CENTER);
        return result;
    }

    private void displayEnemyCards(Pane node, int numberCards,int angel) {
        Platform.runLater(() -> {
            node.getChildren().clear();
            for (int i = 0; i < numberCards; i++) {
                ImageView view = new ImageView(cardBack);
                view.setRotate(angel);
                node.getChildren().add(view);
            }
        });
    }

    private void displayCardsPlayer(ArrayList<Card> cardsList, HBox node) { // отображение карт игрока
        Platform.runLater(() -> {
            node.getChildren().clear();
            int numberCards = 0;
            for ( Card card : cardsList ) {
                numberCards++;
                ImageViewCard view = new ImageViewCard(cards, card.getClone());
                view.setViewport(getCardSprite(card));
                if (node == playerCards)
                    view.setOnMouseClicked(event -> new Thread(new Runnable() {
                        @Override
                        public void run() {
                            controller.move(view.getCard().getClone());
                        }
                    }).start());
                node.getChildren().add(view);
            }
            node.setSpacing(10 + ( -10 * ( numberCards / 3 ) ));
        });
    }

    private void displayCardDeck(Card trumpCard, VBox node) { // отображение колоды
        Platform.runLater(() -> {
            VBox temp = new VBox();
            temp.setAlignment(Pos.CENTER);
            ImageViewCard view = new ImageViewCard(cards, trumpCard);
            view.setRotate(90);
            view.setViewport(getCardSprite(trumpCard));
            temp.getChildren().add(view);
            node.getChildren().add(temp);
            node.getChildren().add(new ImageView(cardBack));
        });
    }

    private Rectangle2D getCardSprite(Card card) {
        return new Rectangle2D(4 + ( 3 + CARD_WIDTH ) * card.getValue(), 3 + ( 3 + CARD_HEIGHT ) * card.getSuit(), CARD_WIDTH, CARD_HEIGHT);
    }

    private Image loadImage(String path) {
        return new Image(getClass().getResourceAsStream(path));
    }
}
