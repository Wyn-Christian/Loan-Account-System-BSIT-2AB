import java.sql.*; 
import groovy.sql.Sql 

class Main {
  static void main(String[] args) {
    LoanAccountSystem app = new LoanAccountSystem();
    app.run();
  }
}

class AppUtilities {
  public static void clrscr(){
      //Clears Screen in java
      try {
          if (System.getProperty("os.name").contains("Windows"))
              new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
          else
              Runtime.getRuntime().exec("clear");
      } catch (IOException | InterruptedException ex) {}
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
    current_amount_borrowed : 10000,
    current_loan : 11000,
    term : 3,
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
    total_paid : this.dummy_status.total_paid_amount,
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

  void run()
  {
    // this.WelcomePage()
    this.UserDashboard()
  }

  void WelcomePage(int error = 0) {
    appUtil.clrscr()
    println "Welcome Page".center(midPadding)
    println "\n\t1] Admin"
    println "\t2] User"
    appUtil.newLn()
    error ? println("\n\tPlease enter valid input...".center(midPadding)) : appUtil.newLn()
    print "Enter: ".padLeft(padLeft)
    
    def answer = input.nextLine();


    switch(answer) {
      case '1':
        this.adminLoginPage()
        break
      case '2':
        this.UserPage()
        break
      default:
        this.WelcomePage(1)
    }
  }
  
  void UserPage(int err = 0) {
    appUtil.clrscr()
    println "User Page"
    println "\n\t1] Register"
    println "\t2] Log-In"
    println "\t3] Return"
    err ? println("\n\tPlease enter valid input...") : print("\n")
    print "\tEnter: "

    def ans = input.nextLine();

    appUtil.clrscr()
    // println("you've entered $ans")
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
        this.UserPage(1)
    }
    
  }

  void UserRegisterPage(int err = 0) {
    def acc = [:]

    appUtil.clrscr();
    println "User Register Page".center(48)
    appUtil.newLn()
    print "username: ".padLeft(padding)
    acc.username = input.next()
    print "password: ".padLeft(padding)
    acc.password = input.next()


    appUtil.clrscr()
    println "User Register Page".center(midPadding)
    appUtil.newLn()
    println "${"username: ".padLeft(padding)}${acc.username}"
    println "${"password: ".padLeft(padding)}${acc.password.replaceAll('.','*')}"
    print "re-enter password: ".padLeft(padding)
    acc.repassword = input.next()

    def err1 = 0
    do {

      appUtil.clrscr()
      println "User Register Page".center(midPadding)
      appUtil.newLn()
      println "Create the account?".center(midPadding)
      err1 ? appUtil.newLn() : null
      println("${err1 ? "Please enter valid input..." : "\n"}".center(midPadding))
      print "Enter[yes/no]: ".padLeft(padding)

      def ans = input.next()
      switch(ans)
      {
        case 'y':
        case 'Y':
        case 'yes':
        case 'Yes':
        case 'YES':
          err1 = 0
          // Pending DB task -----------------------------------
          // Insert new date in user_table
          // proceed to profile information
          this.UserProfileRegisterPage(acc.username);
          break
        case 'n':
        case 'N':
        case 'no':
        case 'No':
        case 'NO':
          err1 = 0
          // then return to welcome page
          this.WelcomePage();
          break
        default:
          err1 = 1
      }
    } while(err1)
  }

  void UserProfileRegisterPage(int err = 0, String username) {
    def acc = [username:username]

    appUtil.clrscr();
    println "User Register Profile Page".center(midPadding)
    appUtil.newLn()
    print "firstname: ".padLeft(padding)
    acc.firstname = input.nextLine()
    print "lastname: ".padLeft(padding)
    acc.lastname = input.nextLine()

    def errAnswer = 0
    def errDate = 0
    def errGender = 0
    acc.birthday = null
    acc.gender = null

    do {
      appUtil.clrscr();
      println "User Register Profile Page".center(midPadding)
      appUtil.newLn()
      println "${"firstname: ".padLeft(padding)}${acc.firstname}"
      println "${"lastname: ".padLeft(padding)}${acc.lastname}"

      // Get birthday input with valid date format
      if(errDate) {
        println "Please enter valid format of date...".center(midPadding)
      }

      
      if(acc.birthday == null) {
        print "birthday(YYYY-MM-DD): ".padLeft(padding)
        acc.birthday = input.nextLine()
        
        if (appUtil.isValidDate(acc.birthday)) {
          errDate = 0;
        } else {
          errDate = 1
          acc.birthday = null
          continue;
        }
      } else {
        println "${"birthday(YYYY-MM-DD): ".padLeft(padding)}${acc.birthday}"
      } 

      // Get gender input with valid format (m|M|f|F)
      if(acc.gender == null) {
        if(errGender) {
          println "Please enter valid format of gender...".padLeft(padding)
        }
        print "gender(M/F): ".padLeft(padding)
        acc.gender = input.nextLine()

        if (appUtil.isValidGender(acc.gender)) {
          errGender = 0;
        } else {
          errGender = 1
          acc.gender = null
          continue;
        }
      } else {
        println "${"gender: ".padLeft(padding)}${acc.gender}"
      }

      appUtil.newLn()
      if(errAnswer) {
          println "Please enter valid answer...".padLeft(padding)
      }
      print "create profile(Y|N): ".padLeft(padding)
      def ans = input.nextLine()
      switch(ans)
      {
        case 'y':
        case 'Y':
        case 'yes':
        case 'Yes':
        case 'YES':
          errAnswer = 0
          // pending DB task ----------------------------------------------
          // Update user's data in user_tbl
          // while displaying "creating account"
          // then return to user page
          this.UserPage();
          break
        case 'n':
        case 'N':
        case 'no':
        case 'No':
        case 'NO':
          errAnswer = 0
          // return to user page
          this.UserPage();
          break
        default:
          errAnswer = 1
      }
    } while( errDate || errGender || errAnswer)

  }

  void UserLoginPage(int err = 0) {
    def acc = [:]

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
      acc.username = input.next()
      if(acc.username == 'return'){
        this.UserPage();
      }
      print "password: ".padLeft(padding)
      acc.password = input.next()
      if(acc.password == 'return'){
        this.UserPage();
      }

      // Verify login
      switch (dummyUser.login(acc.username, acc.password)) {
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
    } while(!loginVerified);

    // proceed to dashboard
    
  }

  void UserDashboard(int err = 0){
    def answer = null;

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
      if(err) {
        println "Please enter valid input".center(midPadding)
      } else {
        appUtil.newLn()
      }

      print "Enter: ".padLeft(padding);
      answer = input.nextLine()

      switch (answer) {
        case '1':
          // Borrow
          this.TransanctBorrow()
          break
        case '2':
          // Pay Loan
          break
        case '3':
          // Account Status
          this.UserAccountPage()
          break
        case '4':
          // Log out
          break
        default:
          // error input
          err = 1;
          answer = null
      }
    } while (err)
  }

  void TransanctBorrow(int err = 0) {
    def amount = "100",
        terms = '3',
        answer = null,
        isDone = [0, 0],
        errAmount = 0,
        errTerms = 0,
        errAnswer = 0

    do {
      appUtil.clrscr()
      println "BORROW DASHBOARD".center(midPadding)
      appUtil.newLn()

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
      } else if (!isDone[0]){
        isDone[0] = 1
        errAmount = 0
        continue
      }
      
      errTerms ? println("Please enter valid input...".center(midPadding)) : null
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

      errAnswer ? println("Please enter valid input...".center(midPadding)) : null
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
          this.RecieptTransaction()
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
    
      println "end of line"
      input.nextLine()
    
  }

  void RecieptTransaction() {
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

  void UserAccountPage(int err = 0) {
    do {
      appUtil.clrscr()
      
      println "ACCOUNT STATUS".center(midPadding)
      appUtil.newLn()
      println "${"Current Amount Borrowed: ".padLeft(padding)}${dummyUser.dummy_status.current_amount_borrowed}"
      println "${"Loan to Pay: ".padLeft(padding)}${dummyUser.dummy_status.current_loan}"
      println "${"Paid Amount: ".padLeft(padding)}${dummyUser.dummy_status.total_paid_amount}"
      println "${"Outstanding Balance: ".padLeft(padding)}${dummyUser.dummy_status.total_balance}"
      println "${"Remainin Term: ".padLeft(padding)}${dummyUser.dummy_status.term} months"

      appUtil.newLn()      
      err ? println ("Please enter valid input...".center(midPadding)) : appUtil.newLn()
      
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

  void adminPage(int err = 0){
     appUtil.clrscr()
    println "Admin Login Page"
    println "\t1] Log-In"
    println "\t2] Return"
    err ? println("\n\tPlease enter valid input...") : print("\n")
    print "\tEnter: "

    def ans = input.nextLine();

    appUtil.clrscr()
    switch(ans) {
      case '1':
        this.adminLoginPage()
        break
      case '2':
        this.WelcomePage()
        break
      default:
        this.adminPage(1)
    }
  }

  void adminLoginPage(int err = 0) {
       def acc = [:]

    def loginVerified = 0,
        isUserExist = null,
        isPasswordRight = null;
    do{
      appUtil.clrscr()
      
      println "Admin Login Page".center(midPadding)
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
      acc.username = input.next()
      if(acc.username == 'return'){
        this.AdminAccountPage();
      }
      print "password: ".padLeft(padding)
      acc.password = input.next()
      if(acc.password == 'return'){
        this.AdminAccountPage();
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
    } while(!loginVerified);
  }


  void adminAccountPage(int err = 0) {

    appUtil.clrscr()
    println "Welcome to Administrator's Page"
    println "\t1] Check Databases"
    println "\t2] Log-out"
    err ? println("\n\tPlease enter valid input...") : print("\n")
    print "\tEnter: "

   appUtil.clrscr()
    // println("you've entered $ans")
    switch(ans) {
      case '1':
        this.Databases()
        break
      case '2':
        this.adminLoginPage()
        break
      default:
        this.WelcomePage(1)
    }
  }
}



