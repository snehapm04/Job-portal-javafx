import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Job_portal extends Application {
    private List<JobListing> jobListings = new ArrayList<>();
    String loggedInUser = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Job Portal");
        Font boldFont = Font.font("System", FontWeight.BOLD, 14);   
        // Load existing job listings from the text file (if any)
        loadJobListingsFromFile("job_listings.txt");
        // UI components
        Button loginAsEmployerButton = new Button("Login as Employer");
        loginAsEmployerButton.setPrefSize(150, 40);
        loginAsEmployerButton.setFont(boldFont);
        Button loginAsJobSeekerButton = new Button("Job Seeker Login");
        loginAsJobSeekerButton.setPrefSize(150, 40);
        loginAsJobSeekerButton.setFont(boldFont);
        // Load the image
        Image backgroundImage = new Image("pic.jpg"); // replace "background.jpg" with the path to your background image
        BackgroundSize backgroundSize = new BackgroundSize(800, 800, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background backgroundStyle = new Background(background);
        // HBox with background image
        HBox hBox = new HBox();
        hBox.setBackground(backgroundStyle);
        hBox.getChildren().addAll(loginAsEmployerButton, loginAsJobSeekerButton);
        hBox.setSpacing(300);
        hBox.setPadding(new Insets(40));

        // Login as Employer Button Action
        loginAsEmployerButton.setOnAction(e -> {
            Optional<String> result = EmployerLoginDialog.showLoginDialog(primaryStage);
            result.ifPresent(username -> {
                loggedInUser = username;
                displayEmployerPage(primaryStage);
            });
        });

        // Job Seeker Login Button Action
        loginAsJobSeekerButton.setOnAction(e -> {
            Optional<String> result = JobSeekerLoginDialog.showLoginDialog(primaryStage);
            result.ifPresent(username -> {
                loggedInUser = username;
                displayJobSeekerPage(primaryStage);
            });
        });
        Scene scene = new Scene(hBox, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Employer Page
    private void displayEmployerPage(Stage primaryStage) {
        Stage employerStage = new Stage();
        employerStage.setTitle("Employer Page");
        Font boldFont = Font.font("System", FontWeight.BOLD, 14);
        // UI components for Employer Page
        Button addJobButton = new Button("Add Job");
        addJobButton.setPrefSize(150, 40);
        addJobButton.setFont(boldFont);
        Button goBackButton = new Button("Go Back");
        goBackButton.setFont(boldFont);
        
        // Load the image
        Image backgroundImage = new Image("pic1.jpg"); // image for employer page
        BackgroundSize backgroundSize = new BackgroundSize(800, 800, true, true, true, false);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background backgroundStyle = new Background(background);

        HBox employerVBox = new HBox();
        employerVBox.setBackground(backgroundStyle);
        employerVBox.getChildren().addAll(addJobButton, goBackButton);
        employerVBox.setSpacing(500);
        employerVBox.setPadding(new Insets(20));

        // Add Job Button Action
        addJobButton.setOnAction(e -> {
            JobListing job = JobListingInputDialog.showAddJobDialog(employerStage);
            if (job != null) {
                jobListings.add(job);
                saveJobListingsToFile("job_listings.txt");
            }
        });

        // Go Back Button Action
        goBackButton.setOnAction(e -> {
            employerStage.close();
            primaryStage.show();
        });

        Scene employerScene = new Scene(employerVBox, 800, 500);
        employerStage.setScene(employerScene);
        employerStage.show();
    }

    private void displayJobSeekerPage(Stage primaryStage) {
        Stage jobSeekerStage = new Stage();
        jobSeekerStage.setTitle("Job Seeker Page");
        Font boldFont = Font.font("System", FontWeight.BOLD, 14);
        // UI components for Job Seeker Page
        Button searchJobsButton = new Button("Search Jobs");
        searchJobsButton.setFont(boldFont);
        Button viewAllJobsButton = new Button("View All Jobs");
        viewAllJobsButton.setFont(boldFont);
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        Button goBackButton = new Button("Go Back");
        goBackButton.setFont(boldFont);

        VBox jobSeekerVBox = new VBox();
        // jobSeekerVBox.setBackground(backgroundStyle);
        jobSeekerVBox.getChildren().addAll(searchJobsButton, viewAllJobsButton, textArea, goBackButton);
        jobSeekerVBox.setSpacing(10);
        jobSeekerVBox.setPadding(new Insets(10));

        // Search Jobs Button Action
        searchJobsButton.setOnAction(e -> {
            String keyword = JobSeekerSearchDialog.showSearchDialog(jobSeekerStage);
            if (keyword != null) {
                List<JobListing> searchResults = jobListings.stream()
                        .filter(job -> job.getTitle().toLowerCase().contains(keyword) ||
                                job.getDescription().toLowerCase().contains(keyword))
                        .collect(Collectors.toList());
                displaySearchResults(searchResults, textArea);
            }
        });

        // View All Jobs Button Action
        viewAllJobsButton.setOnAction(e -> displayJobListings(textArea));

        // Go Back Button Action
        goBackButton.setOnAction(e -> {
            jobSeekerStage.close();
            primaryStage.show();
        });

        Scene jobSeekerScene = new Scene(jobSeekerVBox, 300, 400);
        jobSeekerStage.setScene(jobSeekerScene);
        jobSeekerStage.show();
    }

    private void displaySearchResults(List<JobListing> searchResults, TextArea textArea) {
        textArea.clear();
        for (JobListing job : searchResults) {
            textArea.appendText(job.toString() + "\n");
        }
    }

    private void displayJobListings(TextArea textArea) {
        textArea.clear();
        for (JobListing job : jobListings) {
            textArea.appendText(job.toString() + "\n");
        }
    }

    private void loadJobListingsFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String title = parts[0];
                    String description = parts[1];
                    jobListings.add(new JobListing(title, description));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from the file: " + e.getMessage());
        }
    }

    private void saveJobListingsToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (JobListing job : jobListings) {
                writer.write(job.getTitle() + "," + job.getDescription());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    // Employer Login Dialog Class
    static class EmployerLoginDialog {

        public static Optional<String> showLoginDialog(Stage owner) {
            Dialog<String> dialog = new Dialog<>();
            dialog.initOwner(owner);
            dialog.setTitle("Employer Login");
            dialog.setHeaderText("Enter your username and password");

            TextField usernameField = new TextField();
            PasswordField passwordField = new PasswordField();

            // Set default values for username and password
            usernameField.setText("User");
            passwordField.setText("1908");

            GridPane grid = new GridPane();
            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == loginButton) {
                    // Check if entered credentials match the default values
                    if ("User".equals(usernameField.getText()) && "1908".equals(passwordField.getText())) {
                        return usernameField.getText();
                    }
                }
                return null;
            });

            return dialog.showAndWait();
        }
    }

    // Job Seeker Login Dialog Class
    static class JobSeekerLoginDialog {

        public static Optional<String> showLoginDialog(Stage owner) {
            Dialog<String> dialog = new Dialog<>();
            dialog.initOwner(owner);
            dialog.setTitle("Job Seeker Login");
            dialog.setHeaderText("Enter your username and password");

            TextField usernameField = new TextField();
            PasswordField passwordField = new PasswordField();

            // Set default values for username and password
            usernameField.setText("Hello");
            passwordField.setText("2004");

            GridPane grid = new GridPane();
            grid.add(new Label("Username:"), 0, 0);
            grid.add(usernameField, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(passwordField, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType loginButton = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButton, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == loginButton) {
                    // Check if entered credentials match the default values
                    if ("Hello".equals(usernameField.getText()) && "2004".equals(passwordField.getText())) {
                        return usernameField.getText();
                    }
                }
                return null;
            });

            return dialog.showAndWait();
        }
    }

    // Job Seeker Search Dialog Class
    static class JobSeekerSearchDialog {

        public static String showSearchDialog(Stage owner) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.initOwner(owner);
            dialog.setTitle("Search Jobs");
            dialog.setHeaderText("Enter a keyword to search for jobs");
            dialog.setContentText("Keyword:");

            Optional<String> result = dialog.showAndWait();
            return result.orElse(null);
        }
    }

    // Job Listing Class
    static class JobListing {
        private String title;
        private String description;

        public JobListing(String title, String description) {
            this.title = title;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Title: " + title + ", Description: " + description;
        }
    }

    // Job Input Dialog Class
    static class JobListingInputDialog {

        public static JobListing showAddJobDialog(Stage owner) {
            Dialog<JobListing> dialog = new Dialog<>();
            dialog.initOwner(owner);
            dialog.setTitle("Add Job");
            dialog.setHeaderText("Add a new job listing");

            TextField titleField = new TextField();
            TextArea descriptionArea = new TextArea();

            GridPane grid = new GridPane();
            grid.add(new Label("Title:"), 0, 0);
            grid.add(titleField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descriptionArea, 1, 1);

            dialog.getDialogPane().setContent(grid);

            ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

            dialog.setResultConverter(button -> {
                if (button == addButton) {
                    String title = titleField.getText();
                    String description = descriptionArea.getText();
                    return new JobListing(title, description);
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);
        }
    }
}
