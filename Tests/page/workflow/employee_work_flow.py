from playwright.sync_api import Page, expect
from decimal import Decimal


class Employee_WorkFlow:
    def __init__(self,page: Page):
        self.page = page

        # button
        self.save_change_button = page.get_by_role("button",name="Save Changes")
        self.update_button = page.get_by_role("button",name="Update Ticket")
        self.cancel_button = page.get_by_role("button",name="Cancel")
    def go_to_my_tickets_page(self):
        self.page.get_by_role("link",name="My Tickets").click()

    def ticket_card_by_id(self,ticket_id:str):
        return self.page.locator(".card").filter(has_text=f"#{ticket_id}",)

    def view_ticket_details(self, ticket_id:str):
        ticket = self.ticket_card_by_id(ticket_id)
        ticket.get_by_role("button", name="View Details →").click()

    '''
        save status change 
    '''
    def update_ticket_status(self, status:str):
        # self.page.select_option(".form-select",value=status)
        self.page.get_by_role("combobox").select_option(status)



    '''
        status Assigned -> Quote。-- employee fill amount 
    '''
    def quote_input(self,quote: str):
        value = Decimal(quote)
        self.page.get_by_role("spinbutton").fill(str(value))

    '''
         
    '''
    def confirm_quote(self):
        self.page.get_by_role("button",name="Approve Quote").click()
        self.page.on("dialog", lambda dialog: dialog.accept())

    def cancel_quote(self):
        self.page.get_by_role("button",name="Approve Quote").click()
        self.page.on("dialog", lambda dialog: dialog.dismiss())

    def confirm_payment(self):
        self.page.get_by_role("button",name="Mark as Paid").click()
        self.page.on("dialog", lambda dialog: dialog.accept())

    def cancel_payment(self):
        self.page.get_by_role("button",name="Mark as Paid").click()
        self.page.on("dialog", lambda dialog: dialog.dismiss())

    def confirm_delivered(self):
        self.page.get_by_role("button",name="Mark as Delivered").click()
        self.page.on("dialog", lambda dialog: dialog.accept())

    def cancel_delivered(self):
        self.page.get_by_role("button",name="Mark as Delivered").click()
        self.page.on("dialog", lambda dialog: dialog.dismiss())


    '''
        status ASSIGNED --> QUOTED
    '''

#  Assert status should change to QUOTED after employee fill valid amount
    def assert_status_from_assigned_to_quoted(self):
        expect(self.page.get_by_text("QUOTED").first).to_have_text("QUOTED")
        expect(self.page.get_by_text("QUOTED").nth(1)).to_have_text("QUOTED")
    # Assert status invalid amount input should fail
    def assert_invalid_amount_input_should_fail_quoted(self):
        is_valid = self.page.get_by_role("spinbutton").evaluate("el => el.validity.valid")
        assert is_valid is False

    def assert_should_not_change_to_quoted(self):
        # expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning",has_text="hello"))
        # expect(self.page.locator("badge bg-light text-dark mt-1 border",has_text="hello"))
        expect(self.page.get_by_text("ASSIGNED").first).to_have_text("ASSIGNED")
        expect(self.page.get_by_text("ASSIGNED").nth(1)).to_have_text("ASSIGNED")


    # '''
    #     Status QUOTED --> AWAITING_DEVICE
    # '''
    #
    #
    # #  Assert Status should change from QUOTED to AWAITING_DEVICE after customer confirm quote
    # def assert_status_change_to_awaiting_device_after_confirm(self):
    #     expect(self.page.get_by_text("AWAITING_DEVICE").first).to_have_text("AWAITING_DEVICE")
    #     expect(self.page.get_by_text("AWAITING_DEVICE").nth(1)).to_have_text("AWAITING_DEVICE")
    #
    # # Assert Status should not change with  customer cancel confirm
    # def assert_cancel_confirmation_status_not_changed(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="QUOTED"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="QUOTED"))
    #


    '''
        Status AWAITING_DEVICE -- > DEVICE_RECEIVED
    '''

    # Assert Status should change form AWAITING_DEVICE to DEVICE_RECEIVED with employee update status
    def assert_status_change_to_device_received(self):
        expect(self.page.get_by_text("DEVICE_RECEIVED").first).to_have_text("DEVICE_RECEIVED")
        expect(self.page.get_by_text("DEVICE_RECEIVED").nth(1)).to_have_text("DEVICE_RECEIVED")

    # Assert Status should not change if employee cancel update status
    def assert_status_not_change_to_device_received_with_cancel_update(self):
        expect(self.page.get_by_text("AWAITING_DEVICE").first).to_have_text("AWAITING_DEVICE")
        expect(self.page.get_by_text("AWAITING_DEVICE").nth(1)).to_have_text("AWAITING_DEVICE")

    # # Assert status should not change if employee choose not choose DEVICE_RECEIVED status
    # def assert_status_not_change_without_choose_device_received_as_next_status(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="AWAITING_DEVICE"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="AWAITING_DEVICE"))
    '''
        Status DEVICE_RECEIVED -- > IN_PROGRESS
    '''
    # Assert Status should change form AWAITING_DEVICE to DEVICE_RECEIVED with employee update status
    def assert_status_change_to_in_progress_successfully(self):
        expect(self.page.get_by_text("IN_PROGRESS").first).to_have_text("IN_PROGRESS")
        expect(self.page.get_by_text("IN_PROGRESS").nth(1)).to_have_text("IN_PROGRESS")

    # Assert Status should not change if employee cancel update
    def assert_status_not_change_to_in_progress(self):
        expect(self.page.get_by_text("DEVICE_RECEIVED").first).to_have_text("DEVICE_RECEIVED")
        expect(self.page.get_by_text("DEVICE_RECEIVED").nth(1)).to_have_text("DEVICE_RECEIVED")

    # # Assert status should not change if not choose in progress status
    # def assert_status_not_change_without_choose_in_progress_as_next_satus(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="DEVICE_RECEIVED"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="DEVICE_RECEIVED"))

    '''
        status IN_PROGRESS --> READY_FOR_CONFIRMATION
    '''
    # Assert status should change from IN_PROGRESS to READY_FOR_CONFIRMATION
    def assert_status_should_change_to_ready_for_confirmation(self):
        expect(self.page.get_by_text("READY_FOR_CONFIRMATION").first).to_have_text("READY_FOR_CONFIRMATION")
        expect(self.page.get_by_text("READY_FOR_CONFIRMATION").nth(1)).to_have_text("READY_FOR_CONFIRMATION")

    # Assert status should not change with cancel save status
    def assert_status_should_not_change_to_ready_for_confirmation(self):
        expect(self.page.get_by_text("IN_PROGRESS").first).to_have_text("IN_PROGRESS")
        expect(self.page.get_by_text("IN_PROGRESS").nth(1)).to_have_text("IN_PROGRESS")

    # # Assert status should not change with READY_FOR_CONFIRMATION
    # def assert_status_should_not_change_to_without_choose_ready_for_confirmation_as_next_status(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="IN_PROGRESS"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="IN_PROGRESS"))

    # '''
    #     status READY_FOR_CONFIRMATION --> PAID
    # '''
    # # Assert status should change to paid
    # def assert_status_should_change_to_paid(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="PAID"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="PAID"))
    #
    # #  Assert status  should not change to paid
    # def assert_status_should_not_change_to_paid(self):
    #     expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="READY_FOR_CONFIRMATION"))
    #     expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="READY_FOR_CONFIRMATION"))
    #

    '''
        status PAID --> SHIPPED
    '''
    # Assert status should change to shipped
    def asser_status_should_change_to_shipped(self):
        expect(self.page.get_by_text("SHIPPED").first).to_have_text("SHIPPED")
        expect(self.page.get_by_text("SHIPPED").nth(1)).to_have_text("SHIPPED")

    # Assert status should not change to shipped
    def assert_status_should_not_change_to_shipped(self):
        expect(self.page.get_by_text("PAID").first).to_have_text("PAID")
        expect(self.page.get_by_text("PAID").nth(1)).to_have_text("PAID")


    '''
        status SHIPPED --> DELIVERED
    '''
    #  Assert status should to delivered
    def assert_status_should_change_to_delivered(self):
        expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="DELIVERED"))
        expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="DELIVERED"))
        expect(self.page.get_by_text("SHIPPED").first).to_have_text("DELIVERED")
        expect(self.page.get_by_text("SHIPPED").nth(1)).to_have_text("DELIVERED")

    # Assert status should not change to delivered
    def assert_status_should_not_change_to_delivered(self):
        expect(self.page.locator(".fs-6 px-3 py-2 badge text-dark bg-warning", has_text="SHIPPED"))
        expect(self.page.locator("badge bg-light text-dark mt-1 border", has_text="SHIPPED"))
        expect(self.page.get_by_text("SHIPPED").first).to_have_text("SHIPPED")
        expect(self.page.get_by_text("SHIPPED").nth(1)).to_have_text("SHIPPED")

    '''
        utility 
    '''
    # alert for error message
    def expect_alert(self):
        with self.page.expect_event("dialog") as alert:
            self.save_change_button.click()
        dialog = alert.value
        assert dialog.type == "alert"


    def assert_window_alert(self):
        self.page.once("dialog", lambda dialog: dialog.accept())