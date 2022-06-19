import groovy.sql.Sql 
import java.sql.*
import java.text.NumberFormat;
import java.util.Locale;
import java.text.DecimalFormat 

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
  number,
  term
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
          case 'term':
            prompt_script = 'Payment Term(3,6,9,12)'
            break
          default:
            prompt_script = input_type
        }
        
        answer = this.input prompt_script
        
        if(answer == 'return') {
          user.emptyInput()
          returnpage()
        }

        
        result = user.inputValidator(input_type, answer)
        
        if(result) {
          if (input_type == 'repassword') 
            user.input.repassword = true
          else if (input_type == 'birthday') 
            user.input.birthday = answer.replaceAll(' ', '-')
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

  def user_input_warning(UserAccount user) {
    if(user.has_error) {
      def (i, j) = user.has_error
      def script = user.error_script[i][j - 1]
      this.center script
      
      user.has_error = []
    } else {
      this.center "Enter 'return' to return"
    }
      this.break_line()
  }
  def check_status(UserAccount user, String type, Object errPage){
    switch(type){
      case "borrow":
        if(user.status.current_borrow_ID)
          errPage("Oops..", "Sorry, please pay your existing loan first...")
        break
      case "pay loan":
        if(!user.status.current_borrow_ID)
          errPage("So quiet here...", "Looks like you don't have an existing loan yet...")
        break
      case "status":
        if(!user.status.current_borrow_ID)
          errPage("ACCOUNT STATUS", "You don't have an existing loan yet...")
        break
      default:
        return
    }
  }
  void pause(){read.nextLine()}

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

  def numberToCurrency(Object... number) {

    NumberFormat format = NumberFormat.getCurrencyInstance();
    def result = []
    for(num in number) {
      String currency = format.format(num).replace('$','P ');
      result.add(currency)
    }
    return result
  }
}


class UserAccount {
  DBUtilities DB = new DBUtilities()
  def has_error = []
  def has_no_error = null

  def input = [
    username    : null,
    password    : null,
    firstname   : null,
    lastname    : null,
    birthday    : null,
    gender      : null,
    repassword  : false
  ]

  def profile = [
    ID        : '1',
    username  : 'wynchristian',
    firstname : 'Wyn Christian',
    lastname  : 'Rebanal',
    birthday  : '2002-03-03',
    gender    : 'm',
  ]

  def status = [
    ID                : "1001",
    user_ID           : "1",
    current_borrow_ID : "3001",
    current_loan      : 51000,
    remaining_term    : 2,
    total_balance     : 28900,
    total_paid_amount : 34000,
    date_created      : "2022-01-16 18:00:32",
    date_updated      : "2022-06-16 18:00:32"
  ]

  def current_borrow = [
    ID                : "3001",
    amount            : 50000,
    term              : 6,
    interest          : 0.02,
    total_interest    : 1000,
    principal_amount  : 51000 /*getPrincipalAmount(amount, interest)*/,
    date_created      : "2022-02-17 12:45:03",
    monthly_payment   : 8500 /* getMonthlyPayment(principal_amount, term)*/
  ]

  def latest_payment = [
    ID                : "4001",
    borrow_ID         : "3001",
    amount            : 8500,
    curent_loan_date  : "2022-06-16 18:00:32",
  ]


  def error_script = [
    username : [
      "The username you entered already exist",
      "Please enter valid format of username",
      "username doesn't exist!"
    ],
    password : [
      "Invalid password input format!",
      "Wrong password!",
    ],
    repassword : [
      "Password doesn't match",
    ],
    name : [
      "Invalid name format!"
    ],
    birthday : [
      "Invalid date format!"
    ],
    gender : [
      "Invalid gender format!"
    ],
    amount : [
      "Please enter valid 'amount' format!"
    ],
    term : [
      "Please enter valid term input!",
      "Please choose available payment plan!",
    ],
  ]

  void registerInput() {
    // CREATE user ------------------------------------------------
    DB.createUser(this.input)
    // GET the ID of the new user ------------------------------------------------
    def ID = DB.getUserID(this.input.username, this.input.password)
    // CREATE user status ------------------------------------------------
    DB.createUserStatus(ID)

    this.emptyInput()
  }

  def login() {
    def userID = DB.getIDByUsername(this.input.username);
    if(userID == 0) {
      this.has_error = ['username', 3]
      return false   
    }else if (DB.getPasswordByID(userID) != this.input.password){
      this.has_error = ['password', 2]
      return false
    }

    this.emptyInput()

    this.profile = DB.getProfileByID(userID)
    this.status = DB.getStatusByID(userID)
    return true
  }

  void emptyInput(){
    this.input = [
      username : null,
      password : null,
      firstname : null,
      lastname : null,
      birthday : null,
      gender : null,
      repassword : false
    ]
  }

  void logout() {
    this.profile = [
      ID        : null,
      username  : null,
      firstname : null,
      lastname  : null,
      birthday  : null,
      gender    : null
    ]

    this.status = [
      ID                : null,
      user_ID           : null,
      current_borrow_ID : null,
      current_loan      : null,
      remaining_term    : null,
      total_balance     : null,
      total_paid_amount : null,
      date_created      : null,
      date_updated      : null,
    ]

    this.current_borrow = [
      ID                : null,
      amount            : null,
      term              : null,
      interest          : null,
      total_interest    : null,
      principal_amount  : null,
      date_created      : null,
      monthly_payment   : null,
    ]

    this.latest_payment = [
      ID                : null,
      borrow_ID         : null,
      amount            : null,
      curent_loan_date  : null,
    ]

  }

  void transactBorrow(){
    DB.createLoan(this.profile, this.input)
    this.current_borrow = DB.getCurrentBorrowByUserID(this.profile.ID)

    this.current_borrow.interest = this.getInterestRate(this.current_borrow.term)
    this.current_borrow.principal_amount = this.getPrincipalAmount(this.current_borrow.amount, this.current_borrow.interest)
    this.current_borrow.total_interest = this.current_borrow.principal_amount - this.current_borrow.amount
    this.current_borrow.monthly_payment = this.getMonthlyPayment((double)this.current_borrow.principal_amount, this.current_borrow.term)

    DB.updateStatusBorrow(this.current_borrow);
    this.status = DB.getStatusByID(this.profile.ID)
  }
  
  double getInterestRate(term) {
    switch(term) {
      case '3':
        return 0.015
      case '6':
        return 0.02
      case '9':
        return 0.025
      case '12':
        return 0.036
      default:
        return 0
    }
  }

  double getPrincipalAmount(amount, interest) {
    return (amount * interest) + amount
  }

  double getMonthlyPayment(principal, term) {
    term = Double.parseDouble(term)
    return principal / term
  }

  
  def inputValidator(type, str) {
     def validator = [
      username    : this.&isValidUsername,
      password    : this.&isValidPassword,
      repassword  : this.&validatePassword,
      firstname   : this.&isValidName,
      lastname    : this.&isValidName,
      birthday    : this.&isValidDate,
      gender      : this.&isValidGender,
      amount      : this.&isValidAmount,
      term        : this.&isValidTerm
    ]

    this.has_no_error = validator[type](str)
    
    if(this.has_no_error == true) {

      if (type == 'repassword') 
        this.input.repassword = true
      else{
        this.input[type] = str
      }

    } else {
      this.has_error = [type, this.has_no_error]
    }
    return has_no_error == true ? true : false

  }

  def validatePassword(ans) {
    this.input.password == ans ? true : 1
  }
  
  def isValidDate(String str) {
    return str =~ /^\d{4}[\-|\s|\/](0[1-9]|1[012])[\-|\s|\/](0[1-9]|[12][0-9]|3[01])$/
          ? true
          : 1
  }

  def isValidAmount(String str) {
     str =~ /\b\d+\b/ ? true : 1
  }

  def isValidUsername(String str) {
    // https://mkyong.com/regular-expressions/how-to-validate-username-with-regular-expression/
    //Username requirements
     str =~ /^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$/ ? true : 2
  }

  def isValidGender(String str) {
    if(str.size() > 1)
      return 0
    if(str =~ /[m|M|F|f]{1}/)
      return true
    else
      return 0
  }

  def isValidPassword(String str) {
    // https://mkyong.com/regular-expressions/how-to-validate-password-with-regular-expression/
    str =~ /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\-[{}]:;',?\/*~$^+=<>]).{8,20}$/ ? true : 1
  }

  def isValidName(String str) {
    str =~ /^(?i)[a-z]([- ',.a-z]{0,23}[a-z])?$/ ? true : 1
  }

  def isNum(String str){
     str =~ /\b\d+\b/ ? true : 1
  }

  def isValidTerm(String str){
    if(str =~ /^\d{1,2}$/) {
      switch(str) {
        case '3':
        case '6':
        case '9':
        case '12':
          return true
          break
        default:
          return 2
      }
    } 
    return 1
  }

}


// A class that consists of SQL methods for back end
class DBUtilities {
  // Still in progress
  def sql;

  DBUtilities(){
    this.connect()
  }
  
  void connect() {
    sql = Sql.newInstance('jdbc:mysql://localhost:8080/testdb', 
    'root', '1234', 'com.mysql.cj.jdbc.Driver')

    sql.connection.autoCommit = false
  }
  
  // CREATE ------------------------------------------------------------------------------------------------------------------------
  // Insert new user
	void createUser(inputs) {
    def username = inputs.username,
        password = inputs.password,
        firstname = inputs.firstname,
        lastname = inputs.lastname,
        birthday = inputs.birthday,
        gender = inputs.gender

		def sqlstr = """
				INSERT INTO user_tbl (username, password, firstname, lastname, birthday, gender) 
							VALUES ($username, $password, $firstname, $lastname, $birthday, $gender);
			"""

		try {
				sql.execute(sqlstr);
				sql.commit()
        println "executed successfully!"
		}catch(Exception ex) {
				sql.rollback()
        println "executed failed!"
		}
	}
  // Insert new user status
  void createUserStatus(int userID){
      long d = System.currentTimeMillis();
      def current_date  = new Date(d).format("YYYY-MM-dd HH:mm:ss");

      def sqlstr = """
         INSERT INTO user_status_tbl (user_ID, date_created, date_updated) 
               VALUES ($userID, $current_date, $current_date);
      	"""

      try {
         sql.execute(sqlstr);
         sql.commit()
         println("Successfully created user status") 
      }catch(Exception ex) {
         sql.rollback()
         println("Create user failed") 
      }
  }

  // Insert new borrow data
  void createLoan(profile, inputs){
    long d = System.currentTimeMillis();
    def current_date  = new Date(d).format("YYYY-MM-dd HH:mm:ss");

    def sqlstr = """
        INSERT INTO borrow_tbl (user_ID, amount, term, date_created) 
              VALUES ($profile.ID, $inputs.amount, $inputs.term, $current_date);
      """

    try {
        sql.execute(sqlstr);
        sql.commit()
        println "created loan"
    }catch(Exception ex) {
        sql.rollback()
        println "failed inserting loan"
    }
  }
  

  // UPDATE ------------------------------------------------------------------------------------------------------------------------
  void updateStatusBorrow(borrow) {
    def sqlstr = """
				UPDATE user_status_tbl 
				SET current_borrow_ID = $borrow.ID, 
            current_loan = $borrow.principal_amount, 
            total_balance = $borrow.principal_amount, 
            total_paid_amount = 0,
            date_updated = $borrow.date_created,
            remaining_term = $borrow.term
				WHERE user_ID = $borrow.user_ID;
		"""

		try {
				sql.execute(sqlstr);
				sql.commit()
				println("Successfully upated user's status") 
		}catch(Exception ex) {
				sql.rollback()
				println("Transaction rollback") 
		}

  }
   // READ ------------------------------------------------------------------------------------------------------------------------
  // Select current user's data
  int getUserID(String username, String password) {
    def sqlscript = """
        SELECT ID 
          FROM user_tbl
          WHERE username = $username AND password = $password
    """
   int ID = 0;
   sql.eachRow(sqlscript) {
         tp -> ID = tp.ID
      }  
   return ID;
  }
  
  int getIDByUsername(String name) {
    def sqlscript = """
      SELECT ID 
         FROM user_tbl
         WHERE username = $name
    """
    def result = sql.firstRow(sqlscript);
    return result.ID;
  }

  String getPasswordByID(int ID) {
    def sqlscript = """
      SELECT password 
         FROM user_tbl
         WHERE ID = $ID
    """
    def result = sql.firstRow(sqlscript);
    return result.password;
  }

  def getProfileByID(int ID) {
    def sqlscript = """
      SELECT * 
         FROM user_tbl
         WHERE ID = $ID
    """
    def result = sql.firstRow(sqlscript);
    return result;
  }

  def getStatusByID(int ID) {
    def sqlscript = """
      SELECT * 
         FROM user_status_tbl
         WHERE user_ID = $ID
    """
    def result = sql.firstRow(sqlscript);
    return result;
  }

  def getCurrentBorrowByUserID(int userID) {
    def sqlscript = """
      SELECT * 
         FROM borrow_tbl
         WHERE user_ID = $userID
         ORDER BY ID DESC
    """
    def result = sql.firstRow(sqlscript);
    return result;
  }
   
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
  DummyAdmin dummyAdmin = new DummyAdmin();

  void run() {
    // change this to open directly the page
    this.UserLoginPage()
    
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

    do {
      cli.title "User Register Page"

      cli.user_input_warning user
      
      cli.user_input  user, this.&UserRegisterPage, this.&UserPage,
                      UserInput.username,
                      UserInput.password,
                      UserInput.repassword

      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "Next Page(Y)" 

      switch(result) {
        case 1:
          this.UserProfileRegisterPage()
          break
        case 0:
          user.emptyInput()
          this.WelcomePage()
          break
        default:
          
          break
      }
    } while (true)
  }

  void UserProfileRegisterPage() {
    def isError = 0,
        answer = null,
        result = 0

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
          user.registerInput()
          this.UserPage()
          break
        case 2:
          user.emptyInput()
          this.UserPage()
          break
        default:
          break
      }
    } while (true)

  }

  void UserLoginPage() {
    do{
      cli.title "User Login Page"

      cli.user_input_warning user

      cli.user_input  user, this.&UserLoginPage, this.&WelcomePage,
                      UserInput.username,
                      UserInput.password
      
      if(user.login()){
        this.UserDashboard()
      }
      user.emptyInput()
      continue
      
    } while (true);
  }

  void UserDashboard(){
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.title "WELCOME, $user.profile.firstname"
      cli.center "DASHBOARD" 
      cli.break_line()

      cli.options 5, "Borrow", "Pay loan", "Check Account Status", "Log out"
      
      (isError, answer, result) = cli.prompt_input isError

      if(answer == '4') user.logout()

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

    do {
      cli.check_status user, "borrow", this.&StatusErrorPage
      
      cli.title "BORROW DASHBOARD"

      cli.user_input_warning user
      
      cli.user_input  user, this.&TransanctBorrow, this.&UserDashboard,
                      UserInput.amount,
                      UserInput.term
  
      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Are you sure?(Y|N)" 

      switch(result) {
        case 1:
          // proceed to terms and condition
          this.TermsAndConditions()
          // BD task -----------------------------------------------------------
          user.transactBorrow()
          user.emptyInput()
          this.RecieptBorrowTransaction()
          break
        case 2:
          // return to dashboard
          user.input = []
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

  void RecieptBorrowTransaction() {
    def isError = 0,
        answer = null,
        result = 0
    
    do {
      cli.title "SUMMARY OF BILLING"
      def (amount, total_interest, 
            total_amount, monthly_payment) = cli.numberToCurrency user.current_borrow.amount,
                                                                  user.current_borrow.total_interest,
                                                                  user.current_borrow.principal_amount,
                                                                  user.current_borrow.monthly_payment

      def df = new DecimalFormat("#0.0")
      def interest = df.format(user.current_borrow.interest * 100)

      
      cli.display_data  "Account",             "${user.profile.firstname} ${user.profile.lastname}",
                        "Total Borrowed",      "$amount",
                        "Term",                "${user.current_borrow.term} months",
                        "Interest",            "$interest%",
                        "Total Interest",      "$total_interest",
                        "Total Amount to pay", "$total_amount",
                        "Loan Starting Date",  "${user.current_borrow.date_created.format('yyyy-MM-dd HH:mm:ss')}",
                        "Monthly Payment",     "$monthly_payment"

      (isError, answer, result) = cli.prompt_input isError, InputType.yesNo, "Enter 'Y' to proceed to dashboard"
      if(result) this.UserDashboard()

    } while (true)
  }

  void TransactPayLoan(){
    def isError = 0,
        answer = null,
        result = 0

    do {
      cli.check_status user, "pay loan", this.&StatusErrorPage
      
      cli.title "PAYMENT SECTION" 

      cli.user_input_warning user
      def interest = (int)(user.current_borrow.interest * 100)
      def (amount) = cli.numberToCurrency user.current_borrow.amount
      
      cli.display_data  "Loan Amount",  "$amount",
                        "Term",         "${user.current_borrow.term}",
                        "Interest",     "$interest%"

      cli.user_input  user, this.&TransactPayLoan, this.&UserDashboard,
                      UserInput.amount

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
          user.input = []
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

      def (amount, total_paid) = cli.numberToCurrency user.latest_payment.amount,
                                                      user.status.total_paid_amount
                                                      
      cli.display_data "Account",           "$user.profile.firstname $user.profile.lastname",
                       "Amount Paid",       "$amount",
                       "Total Paid",        "$total_paid",
                       "Remaining Term",    "${user.status.remaining_term} months",
                       "Loan Date",         "${user.current_borrow.date_created}",
                       "Payment Date",      "${user.latest_payment.curent_loan_date}"

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
      cli.check_status user, "status", this.&StatusErrorPage

      cli.title "ACCOUNT STATUS"

      def (amount, loan, paid_amount, balance) = cli.numberToCurrency user.current_borrow.amount,
                                                        user.status.current_loan,
                                                        user.status.total_paid_amount,
                                                        user.status.total_balance

      cli.display_data "Current Amount Borrowed",  "$amount",
                       "Loan to Pay",              "$loan",
                       "Paid Amount",              "$paid_amount",
                       "Outstanding Balance",      "$balance",
                       "Remaining Term",           "$user.status.remaining_term months"

      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "Enter 'Y' to return"
      if(result) this.UserDashboard()

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

  void StatusErrorPage(header, script) {
    def isError = 0,
        answer = null,
        result = 0
    do {
      cli.title header

      cli.center script

      (isError, answer, result) = cli.prompt_input isError, InputType.yes, "Return to Dashboard(Y)"
      result && this.UserDashboard()

    } while (true)
  }
  
}


