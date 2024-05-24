# JADE-FIPA-Communication-between-Agents
This case study explores the development of a MAS using the JADE framework for communication between agents using FIPA protocol. The Multi-Agent System utilizes a set of agents with specific functionalities:

Client Agent manages the client interaction aspect. This includes adding new clients, viewing existing clients stored in the DBMS, and receiving new
orders.

Product Agent interacts with the customer to gather order details such as customer name, product type, quantity, and desired deadline.

Resource Agent currently only receives data via the ACL message. Still, in the future, it is intended that the agent will open and read a JSON file to communicate with a PLC via MODBUS and execute tasks.

# Multi-Agent Systems
There is no universally definition of an agent since different problems require domain-specific elements to be accurately modeled. However, an agent acts upon the environment through actions, and perceives the state of the environment through percepts.

Multi-Agent Systems is an extension of the agent technology where a group of loosely connected autonomous agents acts in an environment to achieve a common goal. Multi-Agent Systems have been adapted in many application domains because of the beneficial advantages offered.

In a MAS, the action of an agent not only modifies its own environment and that of its neighbors. This necessitates that each agent must predict the action of the other agents to decide the optimal action that would be goal-directed. This type of concurrent learning could result in non-stable behavior. The problem is further complicated if the environment is dynamic. Then each agent needs to differentiate between the effects caused by other agent actions and variations in the environment itself.



