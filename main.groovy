import java.sql.*; 
import groovy.sql.Sql 

class Main {
  static void main(String[] args) {
    LoanAccountSystem app = new LoanAccountSystem();
    app.run();
  }
}

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
      this.newLn() 
  }

  void center(String str){
      println str.center(midPadding)
  }
  
  void options(int pad = 0, Object... str){
    str.eachWithIndex {val,i  -> 
      println "${i + 1}.) ".padLeft(padding - pad) + val
    }
    this.newLn()
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
    this.newLn()
  }

  void paragraph(Object... str){
    str.each( val -> println val.padLeft(padding))
  }

  void display_warning_if(isError,  ln = 1, String errScript = "Please enter valid input..." ) {
    isError ? println(errScript.center(midPadding)) : ln ? this.newLn() : null
  }
  
  void newLn() { print "\n"}

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

class DBUtilities {
  // Still in progress
}

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

class LoanAccountSystem {
  Scanner input = new Scanner(System.in);
  CLIUtilities cli = new CLIUtilities();
  DummyUser dummyUser = new DummyUser();
  def padding = 35,
      midPadding = padding * 2

  void run() {
    // change this to open directly the page
    this.UserAccountPage()
  }

  void WelcomePage() {
    int isError = 0
    do {

      cli.title "Welcome Page"
      cli.options 1, "Admin", "User"
      cli.display_warning_if isError
      def answer = cli.input()


      switch(answer) {
        case '1':
          this.AdminLoginPage()
          break
        case '2':
          isError = 0
          this.UserPage()
          break
        default:
          isError = 1
      }
    } while(isError)
  }
  
  void UserPage() {
    int isError = 0
    do {
      cli.title "User Page"
      cli.options 3, "Register", "Log in", "Return"
      cli.display_warning_if isError
      def ans = cli.input()

      switch(ans) {
        case '1':
          this.UserRegisterPage()
          break
        case '2':
          this.UserLoginPage()
          break
        case '3':
          this.WelcomePage()
          break
        default:
          isError = 1
      }
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
      }else {
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

      cli.newLn()
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
        cli.newLn()
      }
      
      cli.newLn()
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
    int err = 0

    do {
      cli.title "WELCOME TO LOAN SYSTEM"
      cli.center "DASHBOARD" 
      cli.options 5, "Borrow", "Pay loan", "Check Account Status", "Log out"
      
      cli.display_warning_if err

      answer = cli.input()
      

      switch (answer) {
        case '1':
          // Borrow
          this.TransanctBorrow()
          break
        case '2':
          // Pay Loan
          this.TransactPayLoan()
          break
        case '3':
          // Account Status
          this.UserAccountPage()
          break
        case '4':
          // Log out
          this.WelcomePage()
          break
        default:
          // error input
          err = 1;
          answer = null
      }
    } while (err)
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
        cli.newLn()
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

      if(cli.isYesNo(ans) == 1){
        this.UserDashboard()
      } else {
        isErr = 1;
      }
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
      
      cli.newLn()
      answer = cli.input "By answering Y and proceeding to the receipt, I have agreed to the terms and conditions: ".padLeft(padding)
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
        amount = null,
        answer = null,
        isErrAnswer = null,
        isDone = 0

    do {
      cli.title "PAYMENT SECTION" 
      cli.display_data  "Loan Amount",  "${dummyUser.dummy_borrow.amount}",
                        "Term",         "${dummyUser.dummy_borrow.term}",
                        "Interest",     "${(int)(dummyUser.dummy_borrow.interest * 100)}%"

      // !isDone || cli.newLn()
    
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

      // cli.newLn()
      cli.display_warning_if isErrAnswer
      answer = cli.input "Are you sure?(Y|N)"

      switch(cli.isYesNo(answer)) {
        case 1:
          // Proceed Receipt Payment
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
    def err = 0;
    do {
      cli.title "RECEIPT PAYMENT"

      cli.display_data "Account",           "${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}",
                       "Amount Paid",       "${dummyUser.dummy_pay_loan.amount}",
                       "Total Paid",        "${dummyUser.dummy_pay_loan.total_paid}",
                       "Remaining Term",    "${dummyUser.dummy_pay_loan.remaining_term} months",
                       "Loan Date",         "${dummyUser.dummy_pay_loan.curent_loan_date}",
                       "Payment Date",      "${dummyUser.dummy_pay_loan.borrow_date}"

      cli.newLn()
      cli.display_warning_if err
      def answer = cli.input "Enter 'y' to proceed to dashboard"
      if(cli.isYesNo(answer) == 1){
        this.UserDashboard()
      } else {
        err = 1;
      }
    } while (err)
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

 
  void AdminPage(int err = 0){
     cli.clrscr()
    println "Admin Login Page"
    println "\t1] Log-In"
    println "\t2] Return"
    err ? println("\n\tPlease enter valid input...") : print("\n")
    print "\tEnter: "

    def ans = input.nextLine();

    cli.clrscr()
    switch(ans) {
      case '1':
        this.AdminLoginPage()
        break
      case '2':
        this.WelcomePage()
        break
      default:
        this.adminPage(1)
    }
  }

  void AdminLoginPage(int err = 0) {
       def acc = [:]

    def loginVerified = 0,
        isUserExist = null,
        isPasswordRight = null;

    do{
      cli.clrscr()
      
      println "Admin Login Page".center(midPadding)
      cli.newLn()
      println "enter 'return' to return". center(midPadding)

      if(isUserExist == 0) {
        println "The username you entered doesn't exist".center(midPadding)
      } else if(isPasswordRight == 0) {
        println "You've entered the wrong password".center(midPadding)
      } else {
        cli.newLn()
      }
      
      cli.newLn()
      print "username: ".padLeft(padding)
      acc.username = input.nextLine()
      if(acc.username == 'return'){
        this.WelcomePage();
      }
      print "password: ".padLeft(padding)
      acc.password = input.nextLine()
      if(acc.password == 'return'){
        this.WelcomePage();
      }

      // Verify login
      switch (dummyAccount.login(acc.username, acc.password)) {
        case 1:
          isUserExist = 0
          break
        case 2:
          isPasswordRight = 0
          break
        case 0:
          isUserExist = 1
          isPasswordRight = 1
          loginVerified = 1
          break
        default: 
          println "error occured in login method...";
      }
    } while(!loginVerified) 
    this.AdminAccountPage()
  }


  void AdminAccountPage(int err = 0) {

    cli.clrscr()
    println "Welcome to Administrator's Page" .center(midPadding)
    println "${"1.) ".padLeft(padding - 7)}Check Databases"
    println "${"2.) ".padLeft(padding - 7)}Logout"
  
    err ? println("Please enter valid input...".center(midPadding)) : print("\n") 
 
    print "Enter: ".padLeft(padding)
    
    def answer = input.nextLine();
  //  cli.clrscr()
    // println("you've entered $ans")
    switch(answer) {
      case '1':
        // this.Databases()
        break
      case '2':
        this.AdminLoginPage()
        break
      default:
        this.AdminAccountPage(1)
    }
  }
}


