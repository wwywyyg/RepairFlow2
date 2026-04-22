from playwright.sync_api import Page, expect
import re

class LoginPage:
    def __init__(self, page: Page):
        self.page = page
        self.email_input = page.get_by_label("Email address")
        self.password_input = page.get_by_label("Password")
        self.login_button = page.get_by_role("button", name="Login")
        self.register_switch_span = page.get_by_text("Register")
        self.login_heading = page.get_by_text("Login to your account")
        self.home_login_button = page.get_by_role("button", name="Register / Login")

    def goto_login_page(self, base_url: str) -> None:
        self.page.goto(f"{base_url}/login")

    def goto_homepage(self, base_url: str) -> None:
        self.page.goto(base_url)

    def navigate_from_homepage_to_login_page(self, base_url: str) -> None:
        self.goto_homepage(base_url)
        self.home_login_button.click()

    def login(self, email: str, password: str) -> None:
        self.email_input.fill(email)
        self.password_input.fill(password)
        self.login_button.click()

    def should_login_page_is_loaded(self) -> None:
        expect(self.login_heading).to_be_visible()
        expect(self.login_button).to_be_visible()
        expect(self.email_input).to_be_visible()
        expect(self.password_input).to_be_visible()

    def should_be_login_success(self) -> None:
        expect(self.page).to_have_url(re.compile(r".*/dashboard$"))
        expect(self.page.get_by_text("Employee Online")).to_be_visible()
        expect(self.page.get_by_text("Dashboard Main Page")).to_be_visible()

    def should_show_alert_message_with_invalid_credentials(self) -> None:
        expect(self.page.get_by_role("alert")).to_contain_text("Invalid username or password")

    def should_show_warning_message_with_invalid_email_formate(self) -> None:
        expect(self.email_input).to_have_attribute("required", "")
        is_valid = self.email_input.evaluate("el => el.validity.valid")
        assert is_valid is False

    def should_show_warning_message_with_empty_password(self) -> None:
        expect(self.password_input).to_have_attribute("required", "")
        is_valid = self.password_input.evaluate("el => el.validity.valid")
        assert is_valid is False