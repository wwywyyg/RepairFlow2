from pathlib import Path
import re
from playwright.sync_api import Page, expect
import os
BASE_URL = os.getenv("BASE_URL", "http://localhost:5173")


class ChatPage:
    def __init__(self, page: Page):
        self.page = page

        self.attach_button = page.get_by_title("Attach File")
        self.message_input = page.get_by_placeholder("Please Enter message....")
        self.send_button = page.get_by_role("button", name="Send")

        self.hidden_file_input = page.locator('input[type="file"]')
        self.preview_image = page.get_by_alt_text("Preview")
        self.attachment_images = page.get_by_alt_text("attachment")
        self.download_button = page.get_by_role("link", name="Download")


    #  open ticket details page
    def go_to_ticket_details_page(self,ticket_id: str):
        self.page.goto(f"{BASE_URL}/dashboard/tickets/{ticket_id}")
        self.message_input.wait_for(state="visible", timeout=10000)
        expect(self.message_input).to_be_editable(timeout=5000)



    # ---------- basic assertions ----------

    def assert_send_disabled(self) -> None:
        expect(self.send_button).to_be_disabled()

    def assert_send_enabled(self) -> None:
        expect(self.send_button).to_be_enabled()

    # ---------- text message ----------

    def fill_message(self, text: str) -> None:
        self.message_input.fill(text)

    def send_text_message(self, text: str) -> None:
        self.fill_message(text)
        expect(self.message_input).to_have_value(text, timeout=5000)
        expect(self.send_button).to_be_enabled(timeout=5000)
        self.send_button.click()

    def assert_text_message_visible(self, text: str) -> None:
        locator = self.page.get_by_text(text)
        try:
            expect(locator).to_be_visible(timeout=20000)
        except Exception:
            print("Current page content:")
            print(self.page.content())
            raise

    # ---------- file upload ----------

    def upload_image_via_file_chooser(self, file_path: str) -> None:
        with self.page.expect_file_chooser() as fc_info:
            self.attach_button.click()
        fc_info.value.set_files(file_path)

    def upload_image_directly(self, file_path: str) -> None:
        self.hidden_file_input.set_input_files(file_path)

    def assert_preview_visible(self) -> None:
        expect(self.preview_image).to_be_visible(timeout=5000)

    def assert_preview_hidden(self) -> None:
        expect(self.preview_image).to_have_count(0)

    def send_current_message(self) -> None:
        self.send_button.click()

    # ---------- chat image assertions ----------

    def assert_attachment_visible(self) -> None:
        expect(self.attachment_images.last).to_be_visible(timeout=5000)

    def assert_attachment_src_valid(self) -> None:
        expect(self.attachment_images.last).to_have_attribute(
            "src",
            re.compile(r"/uploads/|^blob:|^https?://")
        )

    # ---------- viewer modal ----------

    def open_last_attachment(self) -> None:
        self.attachment_images.last.click()

    def assert_download_visible(self) -> None:
        expect(self.download_button).to_be_visible(timeout=5000)

    # ---------- convenience flows ----------

    def send_image_message(self, file_path: str) -> None:
        self.upload_image_via_file_chooser(file_path)
        self.assert_preview_visible()
        self.assert_send_enabled()
        self.send_current_message()

    def send_image_and_text(self, file_path: str, text: str) -> None:
        self.upload_image_via_file_chooser(file_path)
        self.fill_message(text)
        self.assert_send_enabled()
        self.send_current_message()



