from page.Chatpage.Chatpage import ChatPage



IMAGE_PATH = "page/uploads/testImage.jpg"

'''
    empty message not allowed send
'''
def test_customer_chat_workflow(customer_page,prepare_full_workflow_ticket):
    ticket_id = prepare_full_workflow_ticket
    customer_page = ChatPage(customer_page)
    customer_page.go_to_ticket_details_page(ticket_id)
    customer_page.assert_send_disabled()

    # customer should send text message successfully

    customer_page.go_to_ticket_details_page(ticket_id)
    text = "this is customer test message 1111111"

    # input text message
    customer_page.fill_message(text)
    customer_page.assert_send_enabled()
    customer_page.send_current_message()
    # assert
    customer_page.assert_text_message_visible(text)


    # customer should send image successfully
    customer_page.go_to_ticket_details_page(ticket_id)
    customer_page.upload_image_via_file_chooser(IMAGE_PATH)
    customer_page.assert_send_enabled()
    customer_page.assert_preview_visible()
    customer_page.send_current_message()
    customer_page.assert_attachment_visible()
    customer_page.assert_attachment_src_valid()


    # test customer can send image and text together

    customer_page.go_to_ticket_details_page(ticket_id)
    text = "customer image and image send test"

    customer_page.upload_image_via_file_chooser(IMAGE_PATH)
    customer_page.assert_preview_visible()

    customer_page.fill_message(text)
    customer_page.assert_send_enabled()
    customer_page.send_current_message()

    customer_page.assert_attachment_visible()
    customer_page.assert_attachment_src_valid()
    customer_page.assert_text_message_visible(text)