import groovy.sql.Sql 
import java.sql.*


// This is the main program that starts the application
class Main {
  static void main(String[] args) {
    LoanAccountSystem app = new LoanAccountSystem();
    app.run();
  }
}

enum InputType {
  yes,
  yesNo,
  number
} 

enum UserInput {
  username,
  password,
  repassword,
  firstname,
  lastname,
  birthday,
  gender,
  amount,
  number
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

  void display_warning_if(isError, String errScript = "Please enter valid input...", int ln = 1) {
    isError ? println(errScript.center(midPadding)) : ln ? this.break_line() : null
  }

  void break_line() { print "\n"}

   def prompt_input(int isErr, InputType type = 'number', 
                   String str = "Enter",
                   String errStr = "Please enter valid input...",
                   int blank_line = 1) {
    def answer, result

    def types = [
      number: this.&isNum,
      yes: this.&isYes,
      yesNo: this.&isYesNo,
    ]

    this.display_warning_if isErr, errStr, blank_line
    answer = this.input str

    if(result = types["$type"](answer)) {
      isErr = 0
    } else {
      isErr =  1 
      answer = null
    }

    return [isErr, answer, result]
  }

  def user_input(UserAccount user,  Object page, Object returnpage, UserInput... type){
    def answer = null,
        isError = 0,
        result = 0

    for (input_type in type) {
      input_type = input_type.toString()
      
      if(!user.input[input_type]) {
        def prompt_script;
        switch(input_type) {
          case 'firstname':
            prompt_script = 'first name'
            break
          case 'lastname':
            prompt_script = 'last name'
            break
          case 'birthday':
            prompt_script = 'birthday(YYYY-MM-DD)'
            break
          case 'repassword':
            prompt_script = 're-enter password'
            break
          default:
            prompt_script = input_type
        }
        
        answer = this.input prompt_script
        
        if(answer == 'return') returnpage()

        
        result = user.inputValidator(input_type, answer)

        if(result) {
          if (input_type == 'repassword') 
            user.input.repassword = true
          else 
            user.input[input_type] = answer
        } 
        page()
      } else {
        switch(input_type) {
          case 'repassword':
            break
          case 'password':
            this.display_input input_type, user.input[input_type].replaceAll('.','*')
          break
          default:
            this.display_input input_type, user.input[input_type]
        }
      }
    }
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

  int isYes(String str) {
    switch(str){
      case 'y':
      case 'Y':
      case 'yes':
      case 'Yes':
      case 'YES':
        return 1
      default:
        return 0
    }
  }

  def isNum(String str){
     str =~ /\b\d+\b/ ? true : false
  }
}


class UserAccount{
  def has_no_error = null
  def input = [
    username : null,
    password : null,
     firstname : null,
    lastname : null,
    bday : null,
    gender : null,
    repassword : false
  ]
  def register = [
    username : null,
    password : null,
     firstname : null,
    lastname : null,
    bday : null,
    gender : null
  ]
  def profile = [
    firstname : null,
    lastname : null,
    bday : null,
    gender : null,
  ]
  def current_error = null;
  def error_script = [
    username : [
      "The username you entered already exist",
      "Please enter valid format of username"
    ],
    password : [
      "Invalid password input format",
      "Wrong password!",
      "Password doesn't match"
    ],
    name : [
      "Invalid name format!"
    ],
    date : [
      "Invalid date format!"
    ],
    gender : [
      "Invalid gender format!"
    ],
    amount : [
      "Please enter valid 'amount' format!"
    ]
  ]

  def inputValidator(type, str) {
     def validator = [
      username: this.&isValidUsername,
      password: this.&isValidPassword,
      repassword: this.&validatePassword,
      firstname: this.&isValidName,
      lastname: this.&isValidName,
      birthday: this.&isValidDate,
      gender: this.&isValidGender,
      amount: this.&isValidAmount
    ]
    this.has_no_error = validator[type](str)
    
    if(this.has_no_error == true) {

      if (type == 'repassword') 
        this.input.repassword = true
      else 
        this.input[type] = str

    } 
    return has_no_error == true ? true : false

  }

  def validatePassword(ans) {
    input.password == ans ? true : 4 
  }
  
  def isValidDate(String str) {
    return str =~ /^\d{4}[\-|\s|\/](0[1-9]|1[012])[\-|\s|\/](0[1-9]|[12][0-9]|3[01])$/
          ? true
          : false
  }
  def isValidAmount(String str) {
     str =~ /\b\d+\b/ ? true : false
  }

  def isValidUsername(String str) {
  // https://mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/
  //Username requirements
     str =~ /^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$/ ? true : 1
  }

  def isValidGender(String str) {
    if(str.size() > 1)
      return false
    if(str =~ /[m|M|F|f]{1}/)
      return true
    else
      return false
  }

  def isValidPassword(String str) {
    // https://mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
    str =~ /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\-[{}]:;',?\/*~$^+=<>]).{8,20}$/ ? true : 2
  }
  def isValidName(String str) {
    str =~ /^(?i)[a-z]([- ',.a-z]{0,23}[a-z])?$/ ? true : false
  }
  def isNum(String str){
     str =~ /\b\d+\b/ ? true : false
  }

}



// A class that consists of SQL methods for back end
class DBUtilities {
  // Still in progress
}


// Just dummy data for front end
class DummyUser {
  def dummy_profile = [
            ID : '1',
      username : "johnDoe",
      firstname : "John",
      lastname :  "Doe",
      gender :    "m",
      password : "1234",
      birthday : "2001-03-03"
  ];
  def dummy_status = [
        ID :  "1001",
    user_ID :      "1",
    current_borrow_ID : "3001",
    current_loan : 51000,
    remaining_term : 2,
    total_balance : 28900,
    total_paid_amount : 34000,
    date_created : "2022-01-16 18:00:32",
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
    date_created : "2022-02-17 12:45:03",
    monthly_payment : 8500 /* getMonthlyPayment(principal_amount, term)*/

  ]
  def dummy_pay_loan = [
    ID: "4001",
    user_ID: "1",
    borrow_ID: "3001",
    amount : 8500,
    curent_loan_date : "2022-06-16 18:00:32",
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
  void logout(){
    // empty out the maps
  }

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
  UserAccount user = new UserAccount();
  DummyUser dummyUser = new DummyUser();
  DummyAdmin dummyAdmin = new DummyAdmin();

  void run() {
    // change this to open directly the page
    this.UserProfileRegisterPage()
    
  }

  void WelcomePage() {
    def isError = 0,
        answer = null,
        result = 0

    do {

      cli.title "Welcome Page"
      cli.options 1, "Admin", "User"

      (isError, answer, result) = cli.prompt_input isError
      
      cli.link  answer, 
                this.&AdminLoginPage, 
                this.&UserPage

    } while (true)
  }
  
  void UserPage() {
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.title "User Page"
      cli.options 3, "Register", "Log in", "Return"

      (isError, answer, result) = cli.prompt_input isError

      cli.link  answer, 
                this.&UserRegisterPage, 
                this.&UserLoginPage,
                this.&WelcomePage

    
              
    } while (true)
  }

  void UserRegisterPage() {
    def isError = 0,
        answer = null,
        result = 0

    def acc = [:]

    def isDone = 0

    do {
      cli.title "User Register Page"
      
      cli.user_input  user, this.&UserRegisterPage, this.&UserPage,
                UserInput.username,
                UserInput.password,
                UserInput.repassword

      cli.title "User Register Page"
      cli.center "Create the account?"
      
      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Enter(Y|N)" 

      switch(result) {
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
          break
      }
    } while (true)
  }

  void UserProfileRegisterPage(String username) {
    def isError = 0,
        answer = null,
        result = 0

    def acc = [username:username]

    def errAnswer = 0,
        isErrDate = 0,
        isErrGender = 0

    do {
      cli.title "User Register Profile Page"

      cli.user_input  user, this.&UserProfileRegisterPage, this.&UserPage,
                UserInput.firstname,
                UserInput.lastname,
                UserInput.birthday,
                UserInput.gender
      
      cli.break_line()
      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Create Profile?(Y|N)" 

      switch(result) {
        case 1:
          // pending DB task ------------------------------------------------------------------------------
          // Update user's data in user_tbl
          // while displaying "creating account"
          // then return to user page
          this.UserPage()
          break
        case 2:
          // return to user page
          this.UserPage()
          break
        default:
          break
      }
    } while (true)

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
      // DB task -----------------------------------------------------------------------
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
          // DB TASK ------------------------------------------------------------------
          // login user (get the current status)
          // proceed to dashboard
          this.UserDashboard()
          break
        default: 
           err = 1
          println "error occured in login method..."
      }
    } while (true);

  }

  void UserDashboard(){
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.title "WELCOME TO LOAN SYSTEM"
      cli.center "DASHBOARD" 
      cli.options 5, "Borrow", "Pay loan", "Check Account Status", "Log out"
      
      (isError, answer, result) = cli.prompt_input isError

      cli.link  answer,
                this.&TransanctBorrow,
                this.&TransactPayLoan,
                this.&UserAccountPage,
                this.&WelcomePage

    } while (true)
  }

  void TransanctBorrow() {
    def isError = 0,
        answer = null,
        result = 0

    def acc = [:]
    
    def isDone = [0, 0],
        isErrAmount = 0,
        isErrTerms = 0,
        isErrAnswer = 0

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
      
      cli.display_warning_if isErrTerms
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

      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Are you sure?(Y|N)" 

      switch(result) {
        case 1:
          // proceed to terms and condition
          this.TermsAndConditions()
          // BD task -----------------------------------------------------------
          // create borrow data for the current user
          // currentUser.borrowTransaction()
          this.RecieptBorrowTransaction()
          break
        case 2:
          // return to dashboard
          this.UserDashboard()
          break
        case 0:
          answer = null
          continue
        default:
          println "error"
      }
    } while (true)
  }

  void RecieptBorrowTransaction() {
    def isError = 0,
        answer = null,
        result = 0
    
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

      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Enter 'Y' to proceed to dashboard"
      if(result) this.UserDashboard()

    } while (true)
  }

  void TermsAndConditions() {
    def isError = 0,
        answer = null,
        result = 0
    
    do {
      cli.title "Loan Terms and Conditions."

      cli.paragraph "   Company undertakes to make the Loan to Borrower subject to the terms and conditions of",
                    "this Agreement. The Loan shall be proven by the Note, the repayment of which shall be secured by",
                    "the Deed of Trust, Security Documents, and such other collateral as the Company may need.",
                    "According to the conditions of the Note, interest will accrue and principal and interest will be due."
      
      cli.break_line()
      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "I have agreed to the terms and conditions(y)" 
      if(result) return

    } while (true)
    
  }

  void TransactPayLoan(){
    def isError = 0,
        answer = null,
        result = 0

    def isErrAmount = 0,
        isDone = 0,
        amount = null    

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

      cli.break_line()
      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Are you sure?(Y|N)"

      switch(result) {
        case 1:
          // Proceed Receipt Payment
          // Backend task ***************************************************************************************
          // BD Task -----------------------------------------------------------------------------------
          // currentUser.payLoanTransaction()
          this.RecieptPayLoanTransaction()
          break
        case 2:
          // return to dashboard
          this.UserDashboard()
          break
        case 0:
          answer = null
          continue
        default:
          println "error"
          break
      }

    } while (true)
    
  }

  void RecieptPayLoanTransaction() {
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.title "RECEIPT PAYMENT"

      cli.display_data "Account",           "${dummyUser.dummy_profile.firstname} ${dummyUser.dummy_profile.lastname}",
                       "Amount Paid",       "${dummyUser.dummy_pay_loan.amount}",
                       "Total Paid",        "${dummyUser.dummy_status.total_paid_amount}",
                       "Remaining Term",    "${dummyUser.dummy_status.remaining_term} months",
                       "Loan Date",         "${dummyUser.dummy_borrow.date_created}",
                       "Payment Date",      "${dummyUser.dummy_pay_loan.curent_loan_date}"

      cli.break_line()
      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "Enter 'Y' to proceed to dashboard" 
      if(result) this.UserDashboard()
      
    } while (true)
  }
  
  void UserAccountPage() {
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.title "ACCOUNT STATUS"
      
      cli.display_data "Current Amount Borrowed",  "${dummyUser.dummy_borrow.amount}",
                       "Loan to Pay",              "${dummyUser.dummy_status.current_loan}",
                       "Paid Amount",              "${dummyUser.dummy_status.total_paid_amount}",
                       "Outstanding Balance",      "${dummyUser.dummy_status.total_balance}",
                       "Remaining Term",           "${dummyUser.dummy_status.remaining_term} months"

      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "Enter 'Y' to return"
      
      switch(result){
        case 1:
          this.UserDashboard()
        default:
          break
      }
    } while (true)
  }

 
  void AdminPage(){ 
    def isError = 0,
        answer = null,
        result = 0
    
    int isErr = 0 
    do {

      cli.title "Admin Login Page"
      
      cli.options 2, "Log in", "Return"

      (isError, answer, result) = cli.prompt_input isError
    
      cli.link  answer,
                this.&AdminLoginPage,
                this.WelcomePage

    } while (true)
  }

  void AdminLoginPage() {

    def acc = [:]

    def loginVerified = 0,
        isAdminExist = null,
        isPasswordRight = null;

    do {
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

      // Backend task ***************************************************************************************
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
    } while (true) 
    // BD task --------------------------------------------
    // get admin account status
    this.AdminAccountPage()
  }

  void AdminAccountPage() {
    def isError = 0,
        answer = null,
        result = 0
    
    do {

      cli.title "Welcome to Administrator's Page" 
      cli.options 7, "Check Databases", "Log out"

      (isError, answer, result) = cli.prompt_input isError
      
      cli.link  answer,
                this.&AdminDatabase,
                this.&WelcomePage

    } while (true)
  }

  void AdminDatabase() {
    // BackEnd task ***************************************************************************************
    def isError = 0,
        answer = null,
        result = 0
    
    do {

      cli.title "Status of Database"

      cli.input "Press y to return"
      this.AdminAccountPage()
    } while (true)
  }
}


