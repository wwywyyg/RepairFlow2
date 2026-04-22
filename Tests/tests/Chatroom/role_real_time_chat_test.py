from page.Chatpage.Chatpage import ChatPage


from pathlib import Path

IMAGE_PATH = Path(__file__).resolve().parents[2] / "page" / "uploads" / "testImage.jpg"

'''
    check image exists
'''
def test_role_realtime_chat_workflow(customer_page, employee_page, prepare_full_workflow_ticket):
    assert IMAGE_PATH.exists(), f"Image not found: {IMAGE_PATH}"

'''
    customer message  visible to employee
'''
def test_role_realtime_chat_workflow(customer_page,employee_page,prepare_full_workflow_ticket):
    customer_page = ChatPage(customer_page)
    employee_page = ChatPage(employee_page)
    ticket_id = prepare_full_workflow_ticket

    text = "hello,this is message from customer role"

    # role go to ticket page
    customer_page.go_to_ticket_details_page(ticket_id)
    employee_page.go_to_ticket_details_page(ticket_id)

    # customer_page.assert_text_message_visible(text)
    customer_page.send_text_message(text)

    # employee check message visible
    employee_page.assert_text_message_visible(text)


    # customer image visible to employee

    # role go to ticket page
    customer_page.go_to_ticket_details_page(ticket_id)
    employee_page.go_to_ticket_details_page(ticket_id)

    # customer send image
    customer_page.upload_image_via_file_chooser(IMAGE_PATH)
    customer_page.send_current_message()

    # employee check message visible
    employee_page.assert_attachment_visible()
    employee_page.assert_attachment_src_valid()