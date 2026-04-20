from playwright.sync_api import Page, expect
import re


class RegisterPage:
    def __init__(self, page: Page):
        self.page = page

        # from fields
        self.first_name_input = page.get_by_label("First Name")
        self.last_name_input = page.get_by_label("Last Name")
        self.email_input = page.get_by_label("Email address")
        self.password_input = page.get_by_label("Password",exact=True)
        self.confirm_password_input = page.get_by_label("Confirm password",exact=True)
        self.phone_input = page.get_by_label("Phone")

        # buttons link
        self.register_button = page.get_by_role("button", name="Register")
        self.login_switch_span = page.get_by_text("Login", exact=True)
        self.register_switch_span = page.get_by_text("Register", exact=True)

        # page markers
        self.register_heading = page.get_by_role("heading", name="Create a new account")
        self.login_heading = page.get_by_role("heading", name="Login to your account")

    # go to register form
    def goto(self, base_url: str) -> None:
        self.page.goto(f"{base_url}/login")
        self.register_switch_span.click()

    def assert_register_page_loaded(self) -> None:
        expect(self.register_heading).to_be_visible()

    def fill_register_form(
            self,
            first_name: str,
            last_name: str,
            email: str,
            password: str,
            confirm_password: str,
            phone: str,
    ) -> None:
        self.first_name_input.fill(first_name)
        self.last_name_input.fill(last_name)
        self.email_input.fill(email)
        self.password_input.fill(password)
        self.confirm_password_input.fill(confirm_password)
        self.phone_input.fill(phone)

    def submit(self) -> None:
        self.register_button.click()

    def register(
            self,
            first_name: str,
            last_name: str,
            email: str,
            password: str,
            confirm_password: str,
            phone: str,
    ) -> None:
        self.fill_register_form(
            first_name=first_name,
            last_name=last_name,
            email=email,
            password=password,
            confirm_password=confirm_password,
            phone=phone,
        )


    """
    valid info input to register 
    """
    def assert_redirected_to_login_page(self) -> None:
        expect(self.page).to_have_url(re.compile(r".*/login/?$"))
        expect(self.login_heading).to_be_visible()

    """
    invalid email input show invalid warning
    """

    def assert_invalid_email_address(self) -> None:
        expect(self.page.get_by_text("个数必须在0和24之间")).to_be_visible()

    """
    email input is empty show invalid warning
    """
    def assert_email_field_empty(self) -> None:
        expect(self.email_input).to_have_attribute("required","")
        is_valid = self.email_input.evaluate("el => el.checkValidity()")
        assert is_valid is False

    '''
    password is empty show not match warning
    '''
    def assert_password_field_empty(self) -> None:
        expect(self.password_input).to_have_attribute("required","")
        expect(self.register_button).to_be_disabled()


    def assert_password_field_not_match(self) -> None:
        expect(self.register_button).to_be_disabled()
        expect(self.page.locator(".text-danger",has_text="Passwords do not match")).to_be_visible()

    def assert_email_already_exists(self) -> None:
        expect(self.page.get_by_role("alert")).to_contain_text("Email already exists")
        expect(
            self.page.locator('div[role="alert"]', has_text="Email already exists")
        ).to_be_visible()

    def assert_all_form_fields_empty(self) -> None:
        expect(self.register_button).to_be_enabled()