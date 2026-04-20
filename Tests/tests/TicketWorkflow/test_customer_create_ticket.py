from page.Customer.Customer_Create_Ticket import Customer_Create_Ticket

'''
    should create ticket page loaded
'''
def test_should_load_create_ticket_page(customer_page):
    create_ticket_page = Customer_Create_Ticket(customer_page)
    create_ticket_page.create_ticket_link.click()
    create_ticket_page.create_ticket_page_loaded()



'''
    should ticket create successfully
'''
def test_should_create_ticket_page(customer_page):
    create_ticket_page = Customer_Create_Ticket(customer_page)
    create_ticket_page.create_ticket("title","description","1","2")
    create_ticket_page.assert_ticket_created_successfully()



'''
    should not create ticket without category id
'''
def test_should_not_create_ticket_page_without_category_id(customer_page):
    create_ticket_page = Customer_Create_Ticket(customer_page)
    create_ticket_page.create_ticket_link.click()
    create_ticket_page.fill_ticket_title("title2")
    create_ticket_page.fill_ticket_description("description2")
    create_ticket_page.submit_button.click()
    create_ticket_page.assert_category_required_error()


'''
    should not create ticket without issus id 
'''
def test_should_not_create_ticket_page_without_issus_id(customer_page):
    create_ticket_page = Customer_Create_Ticket(customer_page)
    create_ticket_page.create_ticket_link.click()
    create_ticket_page.fill_ticket_title("title2")
    create_ticket_page.fill_ticket_description("description2")
    create_ticket_page.select_device_category("1")
    create_ticket_page.submit_button.click()
    create_ticket_page.assert_issue_type_error()