from playwright.sync_api import Page, expect
import re


class Customer_Create_Ticket:
    def __init__(self, page:Page):
        self.page = page

        # button / dropdown box
        self.create_ticket_link = page.get_by_role("link",name="Create New Ticket")
        self.title_input = page.locator('input[name="title"]')
        self.description_input = page.locator('textarea[name="description"]')
        self.device_category = page.locator('[name="deviceCategoryId"]')
        self.issue_type = page.locator('[name="issueTypeId"]')
        self.submit_button = page.get_by_role("button" ,name="Create Ticket")
        self.update_button = page.get_by_role("button" ,name="Update Ticket")
        self.pending_label = page.get_by_label("PENDING")

    def open_create_ticket_page(self) -> None:
        self.create_ticket_link.click()

    def fill_ticket_title(self,title:str) -> None:
        self.title_input.fill(title)

    def fill_ticket_description(self,description:str) -> None:
        self.description_input.fill(description)

    def select_device_category(self,value:str) -> None:
        self.device_category.select_option(value)

    def select_issue_type(self,value:str) -> None:
        self.issue_type.select_option(value)

    def create_ticket(self) -> None:
        self.submit_button.click()

    def create_ticket(self,
                      title:str,
                      description:str,
                      category_value: str = None,
                      issue_type_value: str = None):
        self.open_create_ticket_page()
        self.fill_ticket_title(title)
        self.fill_ticket_description(description)
        if category_value:
            self.select_device_category(category_value)
        if issue_type_value:
            self.select_issue_type(issue_type_value)
        self.submit_button.click()

    def get_created_ticket_id_from_url(self) -> str:
        expect(self.page).to_have_url(re.compile(r".*/dashboard/tickets/\d+$"))
        current_url = self.page.url
        match = re.search(r"/tickets/(\d+)$", current_url)
        if not match:
            raise AssertionError(f"Cannot extract ticket id from URL: {current_url}")
        return match.group(1)


    def create_ticket_page_loaded(self):
        expect(self.page).to_have_url(re.compile(r".*/dashboard/customer/create-ticket/?$"))
        expect(self.title_input).to_be_visible()
        expect(self.description_input).to_be_visible()
        expect(self.submit_button).to_be_visible()

    def assert_ticket_created_successfully(self):
        expect(self.update_button).to_be_visible()
        expect(self.page.get_by_role("link",name="← Back to List"))

    def assert_category_required_error(self):
        expect(self.device_category).to_be_visible()
        expect(self.device_category).to_have_attribute("required","")
        is_valid = self.device_category.evaluate("el => el.validity.valid")
        assert is_valid is False

    def assert_issue_type_error(self):
        expect(self.issue_type).to_be_visible()
        expect(self.issue_type).to_have_attribute("required","")
        is_valid = self.issue_type.evaluate("el => el.validity.valid")
        assert is_valid is False
