from page.Chatpage.Chatpage import ChatPage

IMAGE_PATH = "page/uploads/testImage.jpg"



    # empty message not allowed send
def test_employee_chat_workflow(employee_page,prepare_full_workflow_ticket):
    ticket_id = prepare_full_workflow_ticket
    employee_page = ChatPage(employee_page)
    employee_page.go_to_ticket_details_page(ticket_id)
    employee_page.assert_send_disabled()

    # employee should send text message successfully

    employee_page.go_to_ticket_details_page(ticket_id)
    text = "this is employee test message 22"

    # input text message
    employee_page.fill_message(text)
    employee_page.assert_send_enabled()
    employee_page.send_current_message()
    # assert
    employee_page.assert_text_message_visible(text)


    # employee should send image successfully

    employee_page.go_to_ticket_details_page(ticket_id)
    employee_page.upload_image_via_file_chooser(IMAGE_PATH)
    employee_page.assert_send_enabled()
    employee_page.assert_preview_visible()
    employee_page.send_current_message()
    employee_page.assert_attachment_visible()
    employee_page.assert_attachment_src_valid()


    # test employee can send image and text together

    employee_page.go_to_ticket_details_page(ticket_id)
    text = "image and test"

    employee_page.upload_image_via_file_chooser(IMAGE_PATH)
    employee_page.assert_preview_visible()

    employee_page.fill_message(text)
    employee_page.assert_send_enabled()
    employee_page.send_current_message()

    employee_page.assert_attachment_visible()
    employee_page.assert_attachment_src_valid()
    employee_page.assert_text_message_visible(text)