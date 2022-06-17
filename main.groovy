import java.sql.*; 
import groovy.sql.Sql 

class Main {
  static void main(String[] args) {
    LoanAccountSystem app = new LoanAccountSystem();
    app.run();
  }
}

class AppUtilities {
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

  void displayWarning(isError, padding, ln = 1, String script = "Please enter valid input..." ) {
    isError ? println(script.center(padding)) : ln ? this.newLn() : null
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
    return str ==~ /\b\d+\b/ ? true : false
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
  AppUtilities appUtil = new AppUtilities();
  DummyUser dummyUser = new DummyUser();
  def padding = 35,
      midPadding = padding * 2

  void run() {
    // change this to open directly the page
    this.WelcomePage()
  }

  void WelcomePage() {
    int error = 0
    do {

      appUtil.clrscr()
      println "Welcome Page".center(midPadding)
      appUtil.newLn()
      println "${"1.) ".padLeft(padding - 1)}Admin"
      println "${"2.) ".padLeft(padding - 1)}User"
      appUtil.newLn()
      appUtil.displayWarning(error, midPadding)
      print "Enter: ".padLeft(padding)
      
      def answer = input.nextLine();


      switch(answer) {
        case '1':
          this.AdminLoginPage()
          break
        case '2':
          error = 0
          this.UserPage()
          break
        default:
          error = 1
      }
    } while(error)
  }
  
  void UserPage() {
    int err = 0
    do {

      appUtil.clrscr()
      println "User Page".center(midPadding)
      appUtil.newLn()
      println "${"1.) ".padLeft(padding - 3)}Register"
      println "${"2.) ".padLeft(padding - 3)}Log in"
      println "${"3.) ".padLeft(padding - 3)}Return"
      appUtil.newLn()
      appUtil.displayWarning(err, midPadding)
      print "Enter: ".padLeft(padding)

      def ans = input.nextLine();

      appUtil.clrscr()
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
          err = 1
      }
    } while (err)
    
  }

  void UserRegisterPage() {
    def acc = [:]

    appUtil.clrscr();
    println "User Register Page".center(midPadding)
    appUtil.newLn()
    print "username: ".padLeft(padding)
    acc.username = input.nextLine()
    print "password: ".padLeft(padding)
    acc.password = input.nextLine()


    appUtil.clrscr()
    println "User Register Page".center(midPadding)
    appUtil.newLn()
    println "${"username: ".padLeft(padding)}${acc.username}"
    println "${"password: ".padLeft(padding)}${acc.password.replaceAll('.','*')}"
    print "re-enter password: ".padLeft(padding)
    acc.repassword = input.nextLine() 

    def err = 0
    do {

      appUtil.clrscr()
      println "User Register Page".center(midPadding)
      appUtil.newLn()
      println "Create the account?".center(midPadding)
      appUtil.displayWarning(err, midPadding )
      print "Enter(Y|N): ".padLeft(padding)

      def ans = input.nextLine()
      switch(appUtil.isYesNo(ans))
      {
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
          err = 1
      }
    } while(err)
  }

  void UserProfileRegisterPage(String username) {
    def acc = [username:username],
        errAnswer = 0,
        errDate = 0,
        errGender = 0

    do {
      appUtil.clrscr();
      println "User Register Profile Page".center(midPadding)
      appUtil.newLn()

      if(acc.firstname == null) {
        print "first name: ".padLeft(padding)
        acc.firstname = input.nextLine()
      } else {
        println "${"first name: ".padLeft(padding)}${acc.firstname}"
      }

      if(acc.lastname == null) {
        print "last name: ".padLeft(padding)
        acc.lastname = input.nextLine()
      }else {
        println "${"last name: ".padLeft(padding)}${acc.lastname}"

      }

      // Get birthday input with valid date format
      appUtil.displayWarning(errDate, "Please enter valid format of date...", midPadding)

      
      if(acc.birthday == null) {
        print "birthday(YYYY-MM-DD): ".padLeft(padding)
        acc.birthday = input.nextLine()
        
        if (appUtil.isValidDate(acc.birthday)) {
          errDate = 0;
        } else {
          errDate = 1
          acc.birthday = null
        }
          continue
      } else {
        println "${"birthday(YYYY-MM-DD): ".padLeft(padding)}${acc.birthday}"
      } 

      // Get gender input with valid format (m|M|f|F)
      if(acc.gender == null) {
        appUtil.displayWarning(errGender,"Please enter valid format of gender...", midPadding)
        print "gender(M/F): ".padLeft(padding)
        acc.gender = input.nextLine()

        if (appUtil.isValidGender(acc.gender)) {
          errGender = 0;
        } else {
          errGender = 1
          acc.gender = null
        }
          continue
      } else {
        println "${"gender: ".padLeft(padding)}${acc.gender}"
      }

      appUtil.newLn()
      appUtil.displayWarning(errAnswer, midPadding)
      print "create profile(Y|N): ".padLeft(padding)
      def ans = input.nextLine()
      switch(appUtil.isYesNo(ans))
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
    } while( errDate || errGender || errAnswer)

  }

  void UserLoginPage() {
    def acc = [:]
    int err = 0

    def loginVerified = 0,
        isUserExist = null,
        isPasswordRight = null;
    do{
      appUtil.clrscr()
      
      println "User Login Page".center(midPadding)
      appUtil.newLn()
      println "enter 'return' to return". center(midPadding)

      if(isUserExist == 0) {
        println "The username you entered doesn't exist".center(midPadding)
      } else if(isPasswordRight == 0) {
        println "You've entered the wrong password".center(midPadding)
      } else {
        appUtil.newLn()
      }
      
      appUtil.newLn()
      print "username: ".padLeft(padding)
      acc.username = input.nextLine()
      if(acc.username == 'return'){
        this.UserPage();
      }
      print "password: ".padLeft(padding)
      acc.password = input.nextLine()
      if(acc.password == 'return'){
        this.UserPage();
      }

      // Verify login
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
          // login user (get the current status)
          // proceed to dashboard
          this.UserDashboard()
          break
        default: 
           err = 1
          println "error occured in login method...";
      }
    } while(!loginVerified || err);

  }

  void UserDashboard(){
    def answer = null
    int err = 0

    do {
      appUtil.clrscr()
      
      println "WELCOME TO LOAN SYSTEM".center(midPadding)
      appUtil.newLn()
      println "DASHBOARD".center(midPadding)
      println "${"1.)".padLeft(padding - 5)} Borrow"
      println "${"2.)".padLeft(padding - 5)} Pay Loan"
      println "${"3.)".padLeft(padding - 5)} Check Account Status"
      println "${"4.)".padLeft(padding - 5)} Log out"
      
      appUtil.newLn()
      appUtil.displayWarning(err, midPadding)

      print "Enter: ".padLeft(padding);
      answer = input.nextLine()

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
    def amount = "100",
        terms = '3',
        answer = null,
        isDone = [0, 0],
        errAmount = 0,
        errTerms = 0,
        errAnswer = 0
      int err = 0
    do {
      appUtil.clrscr()
      println "BORROW DASHBOARD".center(midPadding)
      appUtil.newLn()

      appUtil.displayWarning(errAmount, midPadding, 0)
      if(amount == null){
        print "Enter amount: ".padLeft(padding)
        amount = input.nextLine()
      } else {
        print "Enter amount: ".padLeft(padding)
        print "$amount\n"
      }

      if(!appUtil.isValidAmount(amount)){
        errAmount = 1
        amount = null
        continuefig
      } else if (!isDone[0]){
        isDone[0] = 1
        errAmount = 0
        continue
      }
      
      appUtil.displayWarning(errTerms, midPadding, 0)
      if(terms == null){
        appUtil.newLn()
        print "Payment Month(3,6,9,12): ".padLeft(padding)
        terms = input.nextLine()
      } else {
        print "Payment Month(3,6,9,12): ".padLeft(padding)
        print "$terms\n"
      }

      switch(terms){
        case '3':
        case '6':
        case '9':
        case '12':
          isDone[1] = 1
          errTerms = 0
          break
        default:
          terms = null
          errTerms = 1
      }
      if(!isDone[1]){
        continue
      }

      appUtil.displayWarning(errAnswer, midPadding)
      if(answer == null) {
        appUtil.newLn()
        print "Are you sure?(Y|N): ".padLeft(padding)
      } else  {
        print "Are you sure?(Y|N): ".padLeft(padding)
        print "$answer\n"
      }

      answer = input.nextLine()
      switch(appUtil.isYesNo(answer)) {
        case 1:
          // proceed to terms and condition
          this.TermsAndConditions()
          // currentUser.borrowTransaction()
          this.RecieptBorrowTransaction()
          break
        case 2:
          // return to dashboard
          this.UserDashboard()
          break
        case 0:
          errAnswer = 1
          answer = null
          continue
        default:
          println "error"
      }
    } while(errAmount || errTerms || errAnswer)
  }

  void RecieptBorrowTransaction() {
    def err = 0;
    do {
      appUtil.clrscr()
      println "SUMMARY OF BILLING".center(midPadding)
      appUtil.newLn()

      println "${"Account: ".padLeft(padding)}${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}"
      println "${"Total Borrowed: ".padLeft(padding)}${dummyUser.dummy_borrow.amount}"
      println "${"Term: ".padLeft(padding)}${dummyUser.dummy_borrow.term} months"
      println "${"Interest: ".padLeft(padding)}${dummyUser.dummy_borrow.interest * 100}%"
      println "${"Total Interest: ".padLeft(padding)}${dummyUser.dummy_borrow.total_interest}"
      println "${"Total Amount to pay: ".padLeft(padding)}${dummyUser.dummy_borrow.principal_amount}"
      println "${"Loan Starting Date: ".padLeft(padding)}${dummyUser.dummy_borrow.date_created}"
      println "${"Monthly Payment: ".padLeft(padding)}${dummyUser.dummy_borrow.monthly_payment}"

      appUtil.newLn()
      err ? println("Please enter valid input...".center(midPadding)) : appUtil.newLn()
      print "Enter 'y' to proceed to dashboard: ".padLeft(padding)
      if(appUtil.isYesNo(input.nextLine()) == 1){
        this.UserDashboard()
      } else {
        err = 1;
      }
    } while (err)
  }

  void TermsAndConditions() {
    def errAnswer = 0,
        answer = null
    do {

      appUtil.clrscr()
      println "Loan Terms and Conditions.".center(midPadding)
      appUtil.newLn()

      println "   Company undertakes to make the Loan to Borrower subject to the terms and conditions of".padLeft(padding)
      println "this Agreement. The Loan shall be proven by the Note, the repayment of which shall be secured by".padLeft(padding)
      println "the Deed of Trust, Security Documents, and such other collateral as the Company may need.".padLeft(padding)
      println "According to the conditions of the Note, interest will accrue and principal and interest will be due.".padLeft(padding)
      
      appUtil.newLn()
      print "By answering Y and proceeding to the receipt, I have agreed to the terms and conditions: ".padLeft(padding)
      answer = input.nextLine()
      if(appUtil.isYesNo(answer) == 1) {
        errAnswer = 0
      } else {
        errAnswer = 1
      }
    } while (errAnswer)
    
    return;
  }

  void TransactPayLoan(){
    def errAmount = 0,
        amount = null,
        answer = null,
        errAnswer = null,
        isDone = 0
    do {
    appUtil.clrscr()
    
    println "PAYMENT SECTION".center(midPadding)
    appUtil.newLn()
    println "${"Loan Amount: ".padLeft(padding)}${dummyUser.dummy_borrow.amount}"
    println "${"Term: ".padLeft(padding)}${dummyUser.dummy_borrow.term}"
    println "${"Interest: ".padLeft(padding)}${(int)(dummyUser.dummy_borrow.interest * 100)}%"
    isDone || appUtil.newLn()
    
    errAmount ? println("Please enter valid input...".center(midPadding)) : appUtil.newLn()
      if(amount == null){
        print "Enter amount: ".padLeft(padding)
        amount = input.nextLine()
      } else {
        print "Enter amount: ".padLeft(padding)
        print "$amount\n"
      }

      if(!appUtil.isValidAmount(amount)){
        errAmount = 1
        amount = null
        continue
      } else if (!isDone){
        isDone = 1
        errAmount = 0
        continue
      }

      errAnswer ? println("Please enter valid input...".center(midPadding)) : null
      appUtil.newLn()
      print "Are you sure?(Y|N): ".padLeft(padding)

      answer = input.nextLine()
      switch(appUtil.isYesNo(answer)) {
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
          errAnswer = 1
          answer = null
          continue
        default:
          println "error"
      }

    } while (errAmount || errAnswer)
    
  }

  void RecieptPayLoanTransaction() {
    def err = 0;
    do {
      appUtil.clrscr()
      println "RECEIPT PAYMENT".center(midPadding)
      appUtil.newLn()

      println "${"Account: ".padLeft(padding)}${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}"
      println "${"Amount Paid: ".padLeft(padding)}${dummyUser.dummy_pay_loan.amount}"
      println "${"Total Paid: ".padLeft(padding)}${dummyUser.dummy_pay_loan.total_paid} months"
      println "${"Remaining Term: ".padLeft(padding)}${dummyUser.dummy_pay_loan.remaining_term * 100}%"
      println "${"Loan Date: ".padLeft(padding)}${dummyUser.dummy_pay_loan.borrow_date}"
      println "${"Payment Date: ".padLeft(padding)}${dummyUser.dummy_pay_loan.curent_loan_date}"

      appUtil.newLn()
      err ? println("Please enter valid input...".center(midPadding)) : appUtil.newLn()
      print "Enter 'y' to proceed to dashboard: ".padLeft(padding)
      if(appUtil.isYesNo(input.nextLine()) == 1){
        this.UserDashboard()
      } else {
        err = 1;
      }
    } while (err)
  }
  
  void UserAccountPage() {
    int err = 0
    do {
      appUtil.clrscr()
      
      println "ACCOUNT STATUS".center(midPadding)
      appUtil.newLn()
      println "${"Current Amount Borrowed: ".padLeft(padding)}${dummyUser.dummy_borrow.amount}"
      println "${"Loan to Pay: ".padLeft(padding)}${dummyUser.dummy_status.current_loan}"
      println "${"Paid Amount: ".padLeft(padding)}${dummyUser.dummy_status.total_paid_amount}"
      println "${"Outstanding Balance: ".padLeft(padding)}${dummyUser.dummy_status.total_balance}"
      println "${"Remaining Term: ".padLeft(padding)}${dummyUser.dummy_status.remaining_term} months"

      appUtil.newLn()
      appUtil.displayWarning(err, midPadding)  

      print "Enter 'Y' to return: ".padLeft(padding)
      def answer = input.nextLine()
      
      switch(answer){
        case 'y':
        case 'Y':
          this.UserDashboard()
        default:
          err = 1
      }
    } while (err)
  }

  // Still in progress
  void AdminLoginPage() {
    
    println "Admin Login Page".center(midPadding)
  }

  // Still in progress
  void adminAccountPage() {
    println "Admin Account Page".center(midPadding)

  }
}


