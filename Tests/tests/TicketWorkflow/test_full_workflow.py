from page.workflow.customer_work_flow import Customer_WorkFlow
from page.workflow.employee_work_flow import Employee_WorkFlow

def test_full_ticket_workflow(customer_page, employee_page,prepare_full_workflow_ticket):
    employee_page = Employee_WorkFlow(employee_page)
    customer_page = Customer_WorkFlow(customer_page)

    #  employee claim ticket  --  status : ASSIGNED
    ticket_id = prepare_full_workflow_ticket

    '''
        ==========================================
        status assigned --> quoted
        employee flow 
        ==========================================
    '''

    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.quote_input("123.23")
    employee_page.save_change_button.click()


    '''
        ==========================================
        status quoted --> awaiting device
        customer flow 
        ==========================================
    '''

    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_quote()
    customer_page.assert_status_change_to_awaiting_device_after_confirm()

    '''
        ==========================================
        status awaiting device --> device received  
        employee flow 
        ==========================================
    '''
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("DEVICE_RECEIVED")
    employee_page.save_change_button.click()
    employee_page.assert_status_change_to_device_received()

    '''
        ==========================================
        device received --> in progress   
        employee flow 
        ==========================================
    '''

    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("IN_PROGRESS")
    employee_page.save_change_button.click()
    employee_page.assert_status_change_to_in_progress_successfully()

    '''
        ==========================================
        in progress -->  ready for confirmation
        employee flow 
        ==========================================
    '''
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("READY_FOR_CONFIRMATION")
    employee_page.save_change_button.click()
    employee_page.assert_status_should_change_to_ready_for_confirmation()

    '''
        ==========================================
        ready for confirmation --> paid
        customer flow 
        ==========================================
    '''
    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_payment()
    customer_page.assert_status_should_change_to_paid()

    '''
        ==========================================
        paid --> shipped
        employee flow 
        ==========================================
    '''
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("SHIPPED")
    employee_page.save_change_button.click()
    employee_page.asser_status_should_change_to_shipped()

    '''
        ==========================================
        shipped --> delivered
        customer flow 
        ==========================================
    '''
    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_delivered()
    customer_page.assert_status_should_change_to_delivered()