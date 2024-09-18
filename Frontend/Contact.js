import React, { useState } from 'react';
import { styled, alpha } from '@mui/material/styles';
import './contactForm.css';



export default function MainHeader() {
	const onSubmit = async (event) => {
		event.preventDefault();
		const formData = new FormData(event.target);
	
		formData.append("access_key", "3a26104d-c01f-4e0e-ae77-76bf4b83313e");
	
		const object = Object.fromEntries(formData);
		const json = JSON.stringify(object);
	
		const res = await fetch("http://localhost:8080/api/contact", {
		  method: "POST",
		  headers: {"Content-Type": "application/json"},
		  body: json
		})

		const text = await res.text();
	
		if (res.ok) {
			window.alert('Message successfully sent!'+<br/>+'we\'ll reply you within 2 to 3 working days');
		  console.log("Success", res);
		}
	  };

  return (
	<section id="contact"  className="subscription">
			<div className="container">
				<div className="subscribe-title text-center">
					<h2>
						Anything more to know?
					</h2>
					<p>
						Reach us via our 24/7 support service.
					</p>
				</div>
				<div className="row">
					<div className="col-sm-12">
						<div className="subscription-input-group">
							<form onSubmit={onSubmit}>
								<input type="Name" className="subscription-input-form" placeholder="Enter your name here" name="name"/>
								<input type="email" className="subscription-input-form" placeholder="Enter your email here" name="email"/>
								<textarea className="subscription-input-form-message" placeholder="Enter your message/question here" name="message"></textarea>
								<button type="submit" className="appsLand-btn subscribe-btn" >
									Send
								</button>
							</form>
							
						</div>

					</div>	
					
				</div>
			</div>
			{/*}<div className="subscribe-title text-center">
				
					<p style={{float:'left', marginLeft:'20px'}}>
						Buit by SD02_2024.<br/>
						All copyrights reserved.
					</p>
				</div>{*/}

		</section>
  );
}
