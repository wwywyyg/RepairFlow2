from playwright.sync_api import Page, expect


class employee_claim_ticket:
    def __init__(self,page: Page):
        self.page = page

        # link
        self.available_ticket_link = page.get_by_role("link",name="Available Tickets")
        self.my_ticket_link = page.get_by_role("link",name="My Tickets")

    def ticket_card_by_id(self,ticket_id:str):
        return self.page.locator(".card").filter(has=self.page.get_by_text(f"#{ticket_id}", exact=True))

    def claim_ticket_confirm(self, ticket_id:str) -> None:
        ticket_card = self.ticket_card_by_id(ticket_id)
        self.page.on("dialog", lambda dialog: dialog.accept())

        ticket_card.get_by_role("button", name="Claim Ticket").click()

    def claim_ticket_cancel(self,ticket_id:str) -> None:
        ticket_card = self.ticket_card_by_id(ticket_id)
        self.page.on("dialog", lambda dialog: dialog.dismiss())

        ticket_card.get_by_role("button", name="Claim Ticket").click()

    def go_to_available_tickets(self):
        self.available_ticket_link.click()

    def go_to_my_tickets(self):
        self.my_ticket_link.click()


    '''
        assert list tickets page should loaded
    '''
    def assert_list_tickets_page_loaded(self) -> None:
        ticket_card = self.ticket_card_by_id("1")
        ticket_card_2 = self.ticket_card_by_id("2")

        expect(ticket_card).to_be_visible()
        expect(ticket_card.get_by_text("PENDING")).to_be_visible()

        expect(ticket_card_2).to_be_visible()
        expect(ticket_card_2.get_by_text("PENDING")).to_be_visible()

    def assert_ticket_claim_successfully(self,ticket_id:str) -> None:
        ticket_card = self.ticket_card_by_id(ticket_id)
        expect(ticket_card).to_be_visible()
        expect(ticket_card.get_by_text("ASSIGNED")).to_be_visible()

    def assert_ticket_claim_failed(self,ticket_id:str) -> None:
        ticket_card = self.ticket_card_by_id(ticket_id)
        expect(ticket_card).to_be_visible()
        expect(ticket_card.get_by_text("PENDING")).to_be_visible()










