from page.AuthPage.login_page import LoginPage

import os

HOME_URL = os.getenv("BASE_URL", "http://localhost:5173")


# ---------------------------
# helper
# ---------------------------
def go_to_login_page(guest_page):
    login_page = LoginPage(guest_page)
    login_page.goto_login_page(HOME_URL)
    return login_page


# ---------------------------
# tests
# ---------------------------

'''
should load login page
'''
def test_should_load_login_page(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.should_login_page_is_loaded()


'''
should navigate from homepage to login page
'''
def test_should_home_page_navigate_login_page(guest_page):
    login_page = LoginPage(guest_page)
    login_page.navigate_from_homepage_to_login_page(HOME_URL)
    login_page.should_login_page_is_loaded()


'''
should login successfully with valid credentials
'''
def test_should_login_with_valid_credentials(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.login("employee@test.com", "11223344")
    login_page.should_be_login_success()


'''
should show alert message for non-existent email
'''
def test_should_show_alert_message_with_non_existent_email(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.login("123qw@test.com", "11223344")
    login_page.should_show_alert_message_with_invalid_credentials()


'''
should show error for wrong password
'''
def test_should_show_alert_message_with_wrong_password(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.login("admin@test.com", "11223311111")
    login_page.should_show_alert_message_with_invalid_credentials()


'''
should show warning message with invalid format email
'''
def test_should_show_alert_message_with_invalid_format_email(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.login("1111111", "11112233")
    login_page.should_show_warning_message_with_invalid_email_formate()


'''
should show warning message with empty email
'''
def test_should_show_warning_message_with_empty_email(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.login_button.click()
    login_page.should_show_warning_message_with_invalid_email_formate()


'''
should show warning message with empty password
'''
def test_should_show_warning_message_with_empty_password(guest_page):
    login_page = go_to_login_page(guest_page)
    login_page.email_input.fill("admin@test.com")
    login_page.login_button.click()
    login_page.should_show_warning_message_with_empty_password()