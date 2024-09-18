import React, { useState, useEffect, useRef } from 'react';
import './assets/css/style.css';
import './assets/css/slick.css';
import './assets/css/slick-theme.css';
import testimonials from './clientdata.json';

const ClientBar = () => {
  const [scrolled, setScrolled] = useState(false);
  const [isDragging, setIsDragging] = useState(false);
  const [startX, setStartX] = useState(0);
  const [scrollLeft, setScrollLeft] = useState(0);
  const sliderRef = useRef(null);

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 50) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };

    const handleWheel = (e) => {
      if (sliderRef.current) {
        e.preventDefault();
        sliderRef.current.scrollLeft += e.deltaY;
      }
    };

    window.addEventListener('scroll', handleScroll);
    sliderRef.current.addEventListener('wheel', handleWheel);

    return () => {
      window.removeEventListener('scroll', handleScroll);
      if (sliderRef.current) {
        sliderRef.current.removeEventListener('wheel', handleWheel);
      }
    };
  }, []);

  const startDragging = (e) => {
    setIsDragging(true);
    setStartX(e.pageX - sliderRef.current.offsetLeft);
    setScrollLeft(sliderRef.current.scrollLeft);
  };

  const stopDragging = () => {
    setIsDragging(false);
  };

  const onDragging = (e) => {
    if (!isDragging) return;
    e.preventDefault();
    const x = e.pageX - sliderRef.current.offsetLeft;
    const walk = (x - startX) * 2; // scroll-fast
    sliderRef.current.scrollLeft = scrollLeft - walk;
  };

  return (
    <section id="reviews" className="reviews">
      <div className="section-header">
        <h2>Clients Reviews</h2>
        <p>What our clients say about us</p>
      </div>
      <div className="reviews-content">
        <div
          className="testimonial-carousel"
          style={{ width: "85%", overflow: "hidden", whiteSpace: "nowrap", margin: "auto", cursor: isDragging ? 'grabbing' : 'grab' }}
          ref={sliderRef}
          onMouseDown={startDragging}
          onMouseUp={stopDragging}
          onMouseLeave={stopDragging}
          onMouseMove={onDragging}
        >
          {testimonials.map((testimonial) => (
            <div className="single-testimonial-box" key={testimonial.id} style={{ display: "inline-block" }}>
              <div className="testimonial-description">
                <div className="testimonial-info">
                  <div className="testimonial-img">
                    <img src={testimonial.image} alt="clients" />
                  </div>
                  <div className="testimonial-person">
                    <h2>{testimonial.name}</h2>
                    <h4>{testimonial.location}</h4>
                    <div className="testimonial-person-star">
                      {[...Array(testimonial.rating)].map((star, index) => (
                        <i className="fa fa-star" key={index}></i>
                      ))}
                    </div>
                  </div>
                </div>
                <div className="testimonial-comment">
                  <p>{testimonial.comment}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default ClientBar;
