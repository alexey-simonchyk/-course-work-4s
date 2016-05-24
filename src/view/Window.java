package view;


import controller.Controller;
import model.Card;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.server.ServerPlayer;

import java.util.ArrayList;

public class Window extends Application {
    private final int CARD_HEIGHT = 86;
    private final int CARD_WIDTH = 58;
    private Image cards; // Крестя - 2 , Пика - 3 , Бубна - 1 , Чирва - 0
    private Image cardBack; // колода карт
    private HBox playerCards, firstEnemy;
    private VBox deck, secondEnemy, tableCards;
    private Controller controller;

    public Window() {
        cards = loadImage("cards.png");
        cardBack = loadImage("card_back.png");
    }

    public void setController(Controller controller) { this.controller = controller; }

    public static void applicationBegin(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Fool online");
        playerCards = new HBox();
        tableCards = new VBox();
        deck = new VBox();
        firstEnemy = new HBox();
        secondEnemy = new VBox();

        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-image: url(\"background.jpg\");");
        Scene scene = new Scene(borderPane, 640, 480);

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

        borderPane.setLeft(secondEnemy);
        secondEnemy.setAlignment(Pos.CENTER);
        secondEnemy.setMinWidth(CARD_HEIGHT);

        stage.setScene(scene);
        stage.show();
        new Controller(this);
    }

    public void update(ArrayList<Card> gameCards, ArrayList<Card> playerCards, ArrayList<ServerPlayer> enemies) { // обновление отображения карт на столе
        displayCardsPlayer(playerCards, this.playerCards);
        displayCardsTable(gameCards, tableCards);
        displayCardDeck(new Card(1, 1), deck);
        int temp = enemies.get(0).getNumberCards();
        displayEnemyCards(firstEnemy, temp, 0);
        firstEnemy.setSpacing(10 + ( -10 * ( temp / 3 ) ));
        if (enemies.size() == 2) {
            displayEnemyCards(secondEnemy, enemies.get(0).getNumberCards(), 90);
            secondEnemy.setSpacing( -8 * temp);
        }
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
                ImageViewCard view = new ImageViewCard(cards, card);
                view.setViewport(getCardSprite(card));
                if (node == playerCards)
                    view.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            controller.move(view.getCard());
                            //tableCards.getChildren().add(view);
                        }
                    });
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
