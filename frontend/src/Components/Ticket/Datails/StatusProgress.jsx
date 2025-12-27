import React from 'react';
import { ProgressBar } from 'react-bootstrap';

const StatusProgress = ({status}) => {

    // let status = 'DEVICE_RECEIVED';
    
    //  state node point 
  const STEPS = [
    { label: "Ticket Open", description: "PENDING/ASSIGNED" },
    { label: "Preparation", description: "QUOTED/AWAITING_DEVICE/DEVICE_RECEIVED" },
    { label: "In Repair", description: "IN_PROGRESS/READY_FOR_CONFIRMATION" },
    { label: "Completed", description: "PAID/SHIPPED/DELIVERED" }
  ];


  const getCurrentStepIndex = (currentStatus) => {
    switch (currentStatus) {
      // Step 1 create
      case 'PENDING':
      case 'ASSIGNED':
        return 0;

      // Step 2 prepare
      case 'QUOTED':
      case 'AWAITING_DEVICE':
      case 'DEVICE_RECEIVED':
        return 1;

      // Step 3: In Repair 
      case 'IN_PROGRESS':
      case 'READY_FOR_CONFIRMATION':
        return 2;

      // Step 4: Completed
      case 'PAID':
      case 'SHIPPED':
      case 'DELIVERED':
        return 3;

      default:
        return 0; 
    }
  };

  const currentStep = getCurrentStepIndex(status);

  
  const progressPercent = (currentStep / (STEPS.length - 1)) * 100;

  return (
    <div className="position-relative my-4 mx-4">
      {/* background line*/}
      <div 
        className="position-absolute w-100 px-5" 
        style={{ top: '15px', zIndex: 0 }} 
      >
        <ProgressBar 
          now={progressPercent} 
          variant="success" 
          style={{ height: '4px' }} 
        />
      </div>

      {/* B. node */}
      <div className="d-flex justify-content-between align-items-start position-relative" style={{ zIndex: 1 }}>
        {STEPS.map((step, index) => {
          
          const isFinished = index <= currentStep; 
          const isCurrent = index === currentStep; 

        //    style logic
          const circleClass = isFinished 
            ? "bg-success text-white border-success" // finish with green bg and white text
            : "bg-white text-secondary border-secondary"; // not finished white bg, grey text

          return (
            <div key={index} className="d-flex flex-column align-items-center" style={{ width: '120px' }}>
              
              {/* 1. node */}
              <div 
                className={`rounded-circle d-flex align-items-center justify-content-center border border-2 fw-bold ${circleClass}`}
                style={{ width: '34px', height: '34px', transition: 'all 0.3s' }}
              >
                {/* finished with tick, unfinished with nunber */}
                {isFinished ? '✓' : index + 1}
              </div>

              {/* 2. main status */}
              <span 
                className={`mt-2 fw-bold text-center ${isCurrent ? 'text-success' : 'text-muted'}`}
                style={{ fontSize: '0.9rem' }}
              >
                {step.label}
              </span>

              {/* 3.  specific real status */}
              {isCurrent && (
                <span className="badge bg-light text-dark mt-1 border">
                  {status}
                </span>
              )}

            </div>
          );
        })}
      </div>
    </div>
  );
};

export default StatusProgress;