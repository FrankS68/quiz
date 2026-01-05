package de.witchcafe.quiz.ui;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.nimbusds.jose.shaded.gson.Gson;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import consulting.segieth.base.ui.ViewToolbar;
import consulting.segieth.security.SecurityService;
import consulting.segieth.security.UserProfile;
import de.witchcafe.quiz.Quiz;
import de.witchcafe.quiz.QuizItem;
import de.witchcafe.quiz.QuizItemService;
import de.witchcafe.quiz.QuizService;
import jakarta.annotation.security.PermitAll;

@Route("/quizzes")
@PageTitle("Quiz List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Quiz List")
@PermitAll
class QuizListView extends VerticalLayout {

    private final QuizService quizService;
    private final QuizItemService quizItemService;
    private final SecurityService securityService;

    final Button createBtn;
    final Grid<Quiz> quizGrid;

    QuizListView(
    		QuizService quizService,
    		QuizItemService quizItemService,
    		SecurityService securityService) {
        this.quizService = quizService;
        this.quizItemService = quizItemService;
        this.securityService = securityService;


        MenuBar createMenu = buildQuizMenu(quizItemService);
        createBtn = new Button("Create", event -> startQuiz("some category"));
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        quizGrid = new Grid<>();
        quizGrid.setItems(query -> quizService.list(toSpringPageRequest(query)).stream());
        quizGrid.addColumn(Quiz::getCategory).setHeader("Category");
        quizGrid.addColumn(Quiz::getUser).setHeader("User");
        quizGrid.addComponentColumn(item -> {
            Span userSpan = new Span(item.getUser().getName());
            userSpan.setTitle(item.getUser().getProvider());
            return userSpan;
        }).setHeader("Aktionen");

        quizGrid.setEmptyStateText("You have no quiz to review");
        quizGrid.setSizeFull();
        quizGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().setOverflow(Style.Overflow.HIDDEN);

        ViewToolbar viewToolbar = new ViewToolbar("Quiz List", ViewToolbar.group(createBtn,createMenu));
		add(viewToolbar);
        add(quizGrid);
    }

    public class QuizDialog extends Dialog{

	    HashMap<QuizItem, String> result = new HashMap<QuizItem, String>();
	    List<QuizItem> quizItems;
	    QuizItem quizItem;
	    Span questionSpan;
	    Span answerSpan;
	    Integer quizIndex = 0;
	    
	    public QuizDialog(String category) {
	    	quizItems = quizItemService.findByCategory(category);
	    	Collections.shuffle(quizItems);
	    	quizItems = quizItems.subList(0, 5);
	    	
	    	quizItem = quizItems.get(quizIndex);
		    Button endButton = new Button("End", e -> {
		    	quizService.createQuiz(
		    			category,
		    			securityService.getUserProfile(),
		    			new Gson().toJson(result));
		        quizGrid.getDataProvider().refreshAll();
		        Notification.show("Quiz added", 3000, Notification.Position.BOTTOM_END)
		                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		    	close();	
		    });
		    Button closeButton = new Button("Close", e -> close());
	        getFooter().add(endButton,closeButton);
	        getHeader().add(category);
	        
	        questionSpan = new Span(quizItem.getQuestion());
	        questionSpan.addClickListener(e -> {
		        answerSpan.setVisible(true);
		        answerSpan.setEnabled(true);
	        });
	        
	        answerSpan = new Span(quizItem.getAnswer());
	        answerSpan.setVisible(false);
	        answerSpan.setEnabled(false);
	        answerSpan.addClickListener(e -> {
		        answerSpan.setVisible(false);
		        answerSpan.setEnabled(false);
		        if (++quizIndex >= quizItems.size()) {
			    	quizService.createQuiz(
			    			category,
			    			securityService.getUserProfile(),
			    			new Gson().toJson(result));
			        quizGrid.getDataProvider().refreshAll();
			        Notification.show("Quiz added", 3000, Notification.Position.BOTTOM_END)
			                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
			    	close();	
		        	
		        }
		        quizItem = quizItems.get(quizIndex);
		        questionSpan.setText(quizItem.getQuestion());
		        answerSpan.setText(quizItem.getAnswer());

	        });
	        
	        VerticalLayout dialogLayout = new VerticalLayout(questionSpan,answerSpan);
	        dialogLayout.setPadding(false);
	        dialogLayout.setSpacing(false);
	        dialogLayout.getStyle().set("width", "22em").set("max-width", "100%");
	        add(dialogLayout);
	    }
    }
    
	private MenuBar buildQuizMenu(QuizItemService quizItemService) {
		MenuBar quizMenu = new MenuBar();
        MenuItem droneItem = quizMenu.addItem("Dronen");
        SubMenu droneMenu = droneItem.getSubMenu();
        
        quizItemService.lookupCategories().forEach(result ->{
        	MenuItem quizItem = droneMenu.addItem(result[0].toString());
        	quizItem.addClickListener(event -> {
        		startQuiz(result[0].toString());
        	});
        });
		return quizMenu;
	}

    private void startQuiz(String category) {
    	new QuizDialog(category).open();
    }

}
