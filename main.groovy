import java.sql.*; 
import groovy.sql.Sql 

// This is the main program that starts the application
class Main {
  static void main(String[] args) {
    LoanAccountSystem app = new LoanAccountSystem();
    app.run();
  }
}

// A class that consists of utilities for CLI
class CLIUtilities {
  Scanner read = new Scanner(System.in);
  def padding = 35,
      midPadding = padding * 2
  
  public static void clrscr(){
      //Clears Screen in java
      try {
          if (System.getProperty("os.name").contains("Windows"))
              new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
          else
              Runtime.getRuntime().exec("clear");
      } catch (IOException | InterruptedException ex) {}
  }

  void title(String str){
      this.clrscr()
      println str.center(midPadding)
      this.break_line() 
  }

  void center(String str){
      println str.center(midPadding)
  }
  
  void options(int pad = 0, Object... str){
    str.eachWithIndex {val,i  -> 
      println "${i + 1}.) ".padLeft(padding - pad) + val
    }
    this.break_line()
  }

  String input(String str = "Enter"){
    print (str.padLeft(padding) + ": ")
    def ans = read.nextLine()
    return ans
  }

  void display_input(String input, String name){
    println "${input.padLeft(padding)}: $name"
  }

  void display_data(Object... str){
    str.eachWithIndex {val ,i -> print "${i % 2 ? val : ("\n" + val.padLeft(padding) + ": ")}"}
    this.break_line()
  }

  void link (String choice, Object... page) {
    def index;

    try {
      index = choice.toInteger() - 1
    } catch (e) {
      return;
    }

    if (index < page.length && index != -1) {
      page[index]()
    } else if( index < 0 ){
      return;
    }
  }

  void paragraph(Object... str){
    str.each( val -> println val.padLeft(padding))
  }

  void display_warning_if(isError,  ln = 1, String errScript = "Please enter valid input..." ) {
    isError ? println(errScript.center(midPadding)) : ln ? this.break_line() : null
  }
  
  void break_line() { print "\n"}

  boolean isValidDate(String str) {
    return str =~ /^\d{4}[\-|\s|\/](0[1-9]|1[012])[\-|\s|\/](0[1-9]|[12][0-9]|3[01])$/
          ? true
          : false
  }

  boolean isValidGender(String str) {
    
    if(str.size() > 1)
      return false
    if(str =~ /[m|M|F|f]{1}/)
      return true
    else
      return false
  }

  int isYesNo(String str) {
    switch(str){
      case 'y':
      case 'Y':
      case 'yes':
      case 'Yes':
      case 'YES':
        return 1
      case 'n':
      case 'N':
      case 'no':
      case 'No':
      case 'NO':
        return 2
      default:
        return 0
    }
  }

  boolean isValidAmount(String str) {
    // assert str =~ /\b\d+\b/
    return str =~ /\b\d+\b/ ? true : false
  }
}

// A class that consists of SQL methods for back end
class DBUtilities {
  // Still in progress
}

// Just dummy data for front end
class DummyUser {
  def dummy_profile = [
      ID: '1',
      username : "johnDoe",
      firstname : "John",
      lastname : "Doe",
      gender : "m",
      password : "1234",
      birthday : "2001-03-03"
  ];
  def dummy_status = [
    ID : "1001",
    user_ID : "1",
    current_borrow_ID : "3001",
    current_loan : 11000,
    remaining_term : 6,
    total_balance : 0,
    total_paid_amount : 0,
    date_created : "2022-06-16 18:00:32",
    date_updated : "2022-06-16 18:00:32"
  ]
  def dummy_borrow = [
    ID: "3001",
    user_ID: '1',
    amount : 50000,
    term : 6,
    interest : 0.02,
    total_interest : 1000,
    principal_amount : 51000 /*getPrincipalAmount(amount, interest)*/,
    date_created : "2022-06-17 12:45:03",
    monthly_payment : 8500 /* getMonthlyPayment(principal_amount, term)*/

  ]
  def dummy_pay_loan = [
    ID: "4001",
    user_ID: "1",
    amount : 8500,
    total_paid : 25500,
    remaining_term : 6,
    borrow_date : "2022-06-17 12:45:03",
    curent_loan_date : "2022-06-17 12:45:03",
  ]
  // login
  int login(username, password) {
    if(dummy_profile.username != username){
      return 1
    } else if (dummy_profile.password != password){
      return 2
    } else {
      return 0
    }
  }
  // logout
  // create account
  // update account
  // read current account

  // create status  
  // udpate status  
  // read current status

  // create transaction 
  

}

class DummyAdmin {
  def profile = [
    admin: "johnDoe",
    password: "1234"
  ]
   int login(admin, password) {
    if(profile.admin != admin){
      return 1
    } else if (profile.password != password){
      return 2
    } else {
      return 0
    }
  }
}

// The main application of the system
class LoanAccountSystem {
  CLIUtilities cli = new CLIUtilities();
  DummyUser dummyUser = new DummyUser();
  DummyAdmin dummyAdmin = new DummyAdmin();

  void run() {
    // change this to open directly the page
    this.AdminAccountPage()
  }

  void WelcomePage() {
    int isError = 0
    do {

      cli.title "Welcome Page"
      cli.options 1, "Admin", "User"

      cli.display_warning_if isError
      def answer = cli.input()

      cli.link  answer, 
                this.&AdminLoginPage, 
                this.&UserPage

      isError = 1
    } while(isError)
  }
  
  void UserPage() {
    int isError = 0
    do {
      cli.title "User Page"
      cli.options 3, "Register", "Log in", "Return"
      cli.display_warning_if isError
      def answer = cli.input()
      cli.link  answer, 
                this.&UserRegisterPage, 
                this.&UserLoginPage,
                this.&WelcomePage
      isError = 1
    } while (isError)
  }

  void UserRegisterPage() {
    def acc = [:]

    def isErr = 0,
        isDone = 0

    do {
      cli.title "User Register Page"

      if(!acc.username){
        acc.username = cli.input "username"
      } else {
        cli.display_input "username", acc.username
      }

      if(!acc.password){
        acc.password = cli.input("password")
        continue
      } else {
        cli.display_input "password", acc.password.replaceAll('.','*')
      }

      if (!isDone){
        acc.repassword = cli.input "re-enter password"
      }


      // Verify password and username -----------------------------
      isDone = 1
      
      cli.title "User Register Page"
      cli.center "Create the account?"
      cli.display_warning_if isErr
      def ans = cli.input "Enter(Y|N)"

      switch(cli.isYesNo(ans)) {
        case 1:
          // Pending DB task -----------------------------------
          // Insert new date in user_table
          // proceed to profile information
          this.UserProfileRegisterPage(acc.username);
          break
        case 2:
          // then return to welcome page
          this.WelcomePage();
          break
        default:
          isErr = 1
      }
    } while(isErr || !isDone)
  }

  void UserProfileRegisterPage(String username) {
    def acc = [username:username],
        errAnswer = 0,
        isErrDate = 0,
        isErrGender = 0

    do {
      cli.title "User Register Profile Page"

      if(!acc.firstname) {
        acc.firstname = cli.input "first name"
      } else {
        cli.display_input "first name", acc.firstname
      }

      if(!acc.lastname) {
        acc.lastname = cli.input "last name"
      } else {
        cli.display_input "last name", acc.lastname
      }

      // Get birthday input with valid date format
      cli.display_warning_if isErrDate, "Please enter valid format of date..."
      
      if(!acc.birthday) {
        acc.birthday = cli.input "birthday(YYYY-MM-DD)"
        
        if (cli.isValidDate(acc.birthday)) {
          isErrDate = 0;
        } else {
          isErrDate = 1
          acc.birthday = null
        }
          continue
      } else {
        cli.display_input "birthday(YYYY-MM-DD)", acc.birthday
      } 

      // Get gender input with valid format (m|M|f|F)
      if(!acc.gender) {
        cli.display_warning_if isErrGender, "Please enter valid format of gender..."
        acc.gender = cli.input "gender(M/F)"

        if (cli.isValidGender(acc.gender)) {
          isErrGender = 0;
        } else {
          isErrGender = 1
          acc.gender = null
        }
          continue
      } else {
        cli.display_input "gender", acc.gender
      }

      cli.break_line()
      cli.display_warning_if errAnswer
      def ans = cli.input "create profile(Y|N)"

      switch(cli.isYesNo(ans))
      {
        case 1:
          errAnswer = 0
          // pending DB task ----------------------------------------------
          // Update user's data in user_tbl
          // while displaying "creating account"
          // then return to user page
          this.UserPage();
          break
        case 2:
          errAnswer = 0
          // return to user page
          this.UserPage();
          break
        default:
          errAnswer = 1
      }
    } while( isErrDate || isErrGender || errAnswer)

  }

  void UserLoginPage() {
    def acc = [:]
    int err = 0

    def loginVerified = 0,
        isUserExist = null,
        isPasswordRight = null;

    do{
      cli.title "User Login Page"
      cli.center "Enter 'return' to return"

      if(isUserExist == 0) {
        cli.center "The username you entered doesn't exist" 
      } else if(isPasswordRight == 0) {
        cli.center "You've entered the wrong password" 
      } else {
        cli.break_line()
      }
      
      cli.break_line()
      acc.username = cli.input "username"
      if(acc.username == 'return'){
        this.UserPage();
      }
      
      acc.password = cli.input "password"
      if(acc.password == 'return'){
        this.UserPage();
      }

      // Verify login
      // db task ---------------------------------------
      switch (dummyUser.login(acc.username, acc.password)) {
        case 1:
          isUserExist = 0
          break
        case 2:
          isUserExist = 1
          isPasswordRight = 0
          break
        case 0:
          isUserExist = 1
          isPasswordRight = 1
          loginVerified = 1
          // DB TASK ----------------------------------
          // login user (get the current status)
          // proceed to dashboard
          this.UserDashboard()
          break
        default: 
           err = 1
          println "error occured in login method..."
      }
    } while(!loginVerified || err);

  }

  void UserDashboard(){
    def answer = null
    int isErr = 0

    do {
      cli.title "WELCOME TO LOAN SYSTEM"
      cli.center "DASHBOARD" 
      cli.options 5, "Borrow", "Pay loan", "Check Account Status", "Log out"
      
      cli.display_warning_if isErr

      answer = cli.input()
      cli.link  answer,
                this.&TransanctBorrow,
                this.&TransactPayLoan,
                this.&UserAccountPage,
                this.&WelcomePage
      isErr = 1
    } while (isErr)
  }

  void TransanctBorrow() {
    def acc = [:]
    def answer = null,
        isDone = [0, 0],
        isErrAmount = 0,
        isErrTerms = 0,
        isErrAnswer = 0
      int err = 0

    do {
      cli.title "BORROW DASHBOARD"

      cli.display_warning_if isErrAmount
      if(acc.amount == null){
        acc.amount = cli.input "Enter amount"
      } else {
        cli.display_input "Enter amount", acc.amount 
      }

      if(cli.isValidAmount(acc.amount) && !isDone[0]){
        isDone[0] = 1
        isErrAmount = 0
        continue
      } else if (!isDone[0]) {
        isErrAmount = 1
        acc.amount = null
        continue
      }
      
      cli.display_warning_if isErrTerms, 0
      if(acc.terms == null){
        acc.terms = cli.input "Payment Month(3,6,9,12)"
      } else {
        cli.display_input "Payment Month(3,6,9,12)", acc.terms 
      }

      if(!isDone[1]){
        switch(acc.terms){
          case '3':
          case '6':
          case '9':
          case '12':
            isDone[1] = 1
            isErrTerms = 0
            continue
          default:
            acc.terms = null
            isErrTerms = 1
            continue
        }
      }

      cli.display_warning_if isErrAnswer
      if(!answer) {
        cli.break_line()
        answer = cli.input "Are you sure?(Y|N)"
      } else  {
        cli.display_input "Are you sure?(Y|N)", answer
      }

      switch(cli.isYesNo(answer)) {
        case 1:
          // proceed to terms and condition
          this.TermsAndConditions()
          // BD task -----------------------------------
          // create borrow data for the current user
          // currentUser.borrowTransaction()
          this.RecieptBorrowTransaction()
          break
        case 2:
          // return to dashboard
          this.UserDashboard()
          break
        case 0:
          isErrAnswer = 1
          answer = null
          continue
        default:
          println "error"
      }
    } while(isErrAmount || isErrTerms || isErrAnswer)
  }

  void RecieptBorrowTransaction() {
    def isErr = 0;
    
    do {
      cli.title "SUMMARY OF BILLING"
      
      cli.display_data  "Account",             "${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}",
                        "Total Borrowed",      "${dummyUser.dummy_borrow.amount}",
                        "Term",                "${dummyUser.dummy_borrow.term} months",
                        "Interest",            "${dummyUser.dummy_borrow.interest * 100}%",
                        "Total Interest",      "${dummyUser.dummy_borrow.total_interest}",
                        "Total Amount to pay", "${dummyUser.dummy_borrow.principal_amount}",
                        "Loan Starting Date",  "${dummyUser.dummy_borrow.date_created}",
                        "Monthly Payment",     "${dummyUser.dummy_borrow.monthly_payment}"

      cli.display_warning_if isErr
      def ans = cli.input "Enter 'y' to proceed to dashboard"

      if(cli.isYesNo(ans) == 1) {
        isErr = 0;
        this.UserDashboard()
      } 
      isErr = 1;
      
    } while (isErr)
  }

  void TermsAndConditions() {
    def errAnswer = 0,
        answer = null
    do {
      cli.title "Loan Terms and Conditions."

      cli.paragraph "   Company undertakes to make the Loan to Borrower subject to the terms and conditions of",
                    "this Agreement. The Loan shall be proven by the Note, the repayment of which shall be secured by",
                    "the Deed of Trust, Security Documents, and such other collateral as the Company may need.",
                    "According to the conditions of the Note, interest will accrue and principal and interest will be due."
      
      cli.break_line()
      answer = cli.input "I have agreed to the terms and conditions(y)"
      if(cli.isYesNo(answer) == 1) {
        errAnswer = 0
      } else {
        errAnswer = 1
      }
    } while (errAnswer)
    
    return;
  }

  void TransactPayLoan(){
    def isErrAmount = 0,
        isErrAnswer = null,
        isDone = 0,
        amount = null,
        answer = null

    do {
      cli.title "PAYMENT SECTION" 
      cli.display_data  "Loan Amount",  "${dummyUser.dummy_borrow.amount}",
                        "Term",         "${dummyUser.dummy_borrow.term}",
                        "Interest",     "${(int)(dummyUser.dummy_borrow.interest * 100)}%"

    
      cli.display_warning_if isErrAmount
      if(amount == null){
        amount = cli.input "Enter amount"
      } else {
        cli.display_input "Enter amount", amount 
      }

      if(cli.isValidAmount(amount) && !isDone){
        isDone = 1
        isErrAmount = 0
        continue
      } else if (!isDone){
        isErrAmount = 1
        amount = null
        continue
      }

      // cli.break_line()
      cli.display_warning_if isErrAnswer
      answer = cli.input "Are you sure?(Y|N)"

      switch(cli.isYesNo(answer)) {
        case 1:
          // Proceed Receipt Payment
          // BD Task -----------------------------------
          // currentUser.payLoanTransaction()
          this.RecieptPayLoanTransaction()
          break
        case 2:
          // return to dashboard
          this.UserDashboard()
          break
        case 0:
          isErrAnswer = 1
          answer = null
          continue
        default:
          println "error"
      }

    } while (isErrAmount || isErrAnswer)
    
  }

  void RecieptPayLoanTransaction() {
    def isErr = 0;
    do {
      cli.title "RECEIPT PAYMENT"

      cli.display_data "Account",           "${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}",
                       "Amount Paid",       "${dummyUser.dummy_pay_loan.amount}",
                       "Total Paid",        "${dummyUser.dummy_pay_loan.total_paid}",
                       "Remaining Term",    "${dummyUser.dummy_pay_loan.remaining_term} months",
                       "Loan Date",         "${dummyUser.dummy_pay_loan.curent_loan_date}",
                       "Payment Date",      "${dummyUser.dummy_pay_loan.borrow_date}"

      cli.break_line()
      cli.display_warning_if isErr
      def answer = cli.input "Enter 'y' to proceed to dashboard"
      if(cli.isYesNo(answer) == 1) {
        isErr = 0
        this.UserDashboard()
      } 
        isErr = 1
    } while (isErr)
  }
  
  void UserAccountPage() {
    int err = 0
    do {
      cli.title "ACCOUNT STATUS"
      
      cli.display_data "Current Amount Borrowed",  "${dummyUser.dummy_borrow.amount}",
                       "Loan to Pay",              "${dummyUser.dummy_status.current_loan}",
                       "Paid Amount",              "${dummyUser.dummy_status.total_paid_amount}",
                       "Outstanding Balance",      "${dummyUser.dummy_status.total_balance}",
                       "Remaining Term",           "${dummyUser.dummy_status.remaining_term} months"

      cli.display_warning_if err  
      def answer = cli.input "Enter 'Y' to return"
      
      switch(answer){
        case 'y':
        case 'Y':
          this.UserDashboard()
        default:
          err = 1
      }
    } while (err)
  }

 
  void AdminPage(){ 
    int isErr = 0 
    do {

      cli.title "Admin Login Page"
      cli.options 2, "Log in", "Return"

      cli.display_warning_if isErr
      def ans = cli.input()
    
      switch(ans) {
        case '1':
          isErr = 0
          this.AdminLoginPage()
          break
        case '2':
          isErr = 0
          this.WelcomePage()
          break
        default:
        isErr = 1
      }
    }while(isErr)
  }

  void AdminLoginPage() {
    def acc = [:]

    def loginVerified = 0,
        isAdminExist = null,
        isPasswordRight = null;

    do{
      cli.title "Admin Login Page"
      cli.center "Enter 'return' to return" 

      if(isAdminExist == 0) {
        cli.center "the Admin you've entered doesn't exist" 
      } else if(isPasswordRight == 0) {
        cli.center "You've entered the wrong password" 
      } else {
        cli.break_line()
      }
      
      acc.admin = cli.input "Admin"
      if(acc.admin == 'return') {
        this.WelcomePage();
      }
      acc.password = cli.input "password"
      if(acc.password == 'return') {
        this.WelcomePage();
      }

      // Verify login
      switch (dummyAdmin.login(acc.admin, acc.password)) {
        case 1:
          isAdminExist = 0
          break
        case 2:
          isAdminExist = 1
          isPasswordRight = 0
          break
        case 0:
          isAdminExist = 1
          isPasswordRight = 1
          loginVerified = 1
          break
        default: 
          println "error occured in login method...";
      }
    } while(!loginVerified) 
    // BD task --------------------------------------------
    // get admin account status
    this.AdminAccountPage()
  }


  void AdminAccountPage() {

    int isErr = 0
    do{

      cli.title "Welcome to Administrator's Page" 
      cli.options 7, "Check Databases", "Log out"

      cli.display_warning_if isErr
      def answer = cli.input()
      
      cli.link  answer,
                this.&AdminDatabase,
                this.&WelcomePage

      isErr = 1
    } while (isErr)
  }

  void AdminDatabase() {
    cli.title "Status of Database"

    cli.input "Press y to return"
    this.AdminAccountPage()
  }
}


